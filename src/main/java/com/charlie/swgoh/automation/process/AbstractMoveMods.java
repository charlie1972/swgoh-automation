package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.MoveStatus;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.CharacterModsScreen;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractMoveMods extends AbstractProcess {

  private final boolean startImmediately;

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private enum ModProcessResult {
    ALREADY_ASSIGNED,
    FOUND_AND_ASSIGNED,
    NOT_FOUND
  }

  protected AbstractMoveMods(boolean startImmediately) {
    this.startImmediately = startImmediately;
  }

  protected abstract String getFileNameSuffix();

  protected void perform(FileUtil.FileComponents fileComponents, Map<String, List<Mod>> modMap, boolean isDryRun) throws Exception {
    if (!CharacterModsScreen.waitForCharactersTab() || !CharacterModsScreen.checkModsCheckbox()) {
      throw new ProcessException("You must start in the characters tab, with mods shown. Aborting.");
    }

    LOG.info("Starting moving mods");
    if (isDryRun) {
      LOG.info("DRY RUN");
    }
    else {
      LOG.info("LIVE RUN");
      if (startImmediately) {
        AutomationUtil.waitForFixed(1000L);
      }
      else {
        for (int countdown = 10; countdown > 0; countdown--) {
          setMessage("WARNING: LIVE RUN. If you wish to abort, type Ctrl-Shift-Q. Starting in " + countdown + "s");
          AutomationUtil.waitForFixed(1000L);
          handleKeys();
        }
      }
    }

    String reportFile = fileComponents
            .withFileName(fileComponents.getFileName() + "-" + getFileNameSuffix() + "-report-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()))
            .withExtension("csv")
            .toString();
    String moveStatusFile = fileComponents
            .withFileName(fileComponents.getFileName() + "-" + getFileNameSuffix())
            .withExtension("status")
            .toString();

   FileUtil.deleteFileIfExists(reportFile);

    MoveStatus moveStatus;
    if (FileUtil.exists(moveStatusFile)) {
      moveStatus = JsonConnector.readObjectFromFile(moveStatusFile, MoveStatus.class);
      moveStatus.getAttention().clear();
    }
    else {
      moveStatus = new MoveStatus();
      moveStatus.setAttention(new ArrayList<>());
      moveStatus.setDone(new ArrayList<>());
      moveStatus.setToProcess(new ArrayList<>(modMap.keySet()));
      JsonConnector.writeObjectToFile(moveStatus, moveStatusFile);
    }

    List<String> alreadyProcessedCharacters = moveStatus.getDone();
    LOG.info("Characters already processed: {}", alreadyProcessedCharacters);
    alreadyProcessedCharacters.forEach(modMap.keySet()::remove);

    FileUtil.writeToFile(
            reportFile,
            "Character name;Result;Slot;Set;Dots;Tier;Primary stat;Secondary stat 1;Secondary stat 2;Secondary stat 3;Secondary stat 4"
    );

    int numberOfItemsToProcess = modMap.values().stream().map(List::size).reduce(0, Integer::sum) + modMap.size();
    int numberOfProcessedItems = 0;
    for (Map.Entry<String, List<Mod>> entry : modMap.entrySet()) {
      handleKeys();
      updateProgressAndETA();

      String characterName = entry.getKey();
      String message = "Character: " + characterName;
      LOG.info(message);
      setMessage(message);

      if (!CharacterModsScreen.waitForCharactersTab()) {
        throw new ProcessException("Character mods screen: characters tab not found. Aborting.");
      }

      AutomationUtil.waitFor(250L);
      CharacterModsScreen.filterName(characterName);
      AutomationUtil.waitFor(250L);
      CharacterModsScreen.enterModScreen(characterName);
      if (!ModScreen.waitForSortButton()) {
        throw new ProcessException("Mod screen: sort button not found. Aborting.");
      }
      if (!ModScreen.checkName(characterName)) {
        throw new ProcessException("Mod screen: name on screen doesn't match " + characterName + ". Aborting.");
      }
      numberOfProcessedItems++;
      progress = (double)numberOfProcessedItems / (double)numberOfItemsToProcess;
      updateProgressAndETA();

      boolean allModsOk = true;
      for (Mod mod : entry.getValue()) {
        String message2 = "Character: " + characterName + ", Slot: " + mod.getSlot();
        LOG.info(message2);
        setMessage(message2);

        ModProcessResult result = processMod(mod);
        LOG.info("Process mod: {}", result);
        AutomationUtil.waitFor(500L);
        if (!ModScreen.waitForSortButton()) {
          throw new ProcessException("Mod screen: sort button not found. Aborting.");
        }
        allModsOk = allModsOk && (result == ModProcessResult.ALREADY_ASSIGNED || result == ModProcessResult.FOUND_AND_ASSIGNED);
        FileUtil.writeToFile(
                reportFile,
                Stream.concat(
                        Stream.of(
                                characterName,
                                result.toString(),
                                mod.getSlot().toString(),
                                mod.getSet().toString(),
                                String.valueOf(mod.getDots()),
                                mod.getTier().toString(),
                                mod.getPrimaryStat().toString()
                        ),
                        mod.getSecondaryStats().stream().map(Object::toString)
                ).collect(Collectors.joining(";"))
        );

        numberOfProcessedItems++;
        progress = (double)numberOfProcessedItems / (double)numberOfItemsToProcess;
        updateProgressAndETA();
      }

      // Finalizing
      AutomationUtil.waitFor(750L);
      // There are cases where the mod stats panels are open, we have to close them first
      if (ModScreen.checkForMinusButton()) {
        AutomationUtil.click(ModScreen.R_MINUS_BUTTON, "Clicking on minus button");
        AutomationUtil.waitFor(1000L);
      }
      // Confirm or revert if the buttons are present
      // Otherwise do nothing, the character's mods have not been changed
      if (ModScreen.checkForRevertButton()) {
        if (!isDryRun) {
          AutomationUtil.click(ModScreen.L_CONFIRM_BUTTON, "Clicking on confirm button");
        }
        else {
          AutomationUtil.click(ModScreen.R_REVERT_BUTTON, "Clicking on revert button");
          AutomationUtil.waitFor(1000L);
          if (!ModScreen.checkForDialogBoxOk()) {
            throw new ProcessException("Mod screen: dialog box OK not found. Aborting.");
          }
          AutomationUtil.click(ModScreen.R_DIALOG_BOX_OK, "Clicking on dialog box OK");
        }
        AutomationUtil.waitFor(1000L);
      }

      if (!ModScreen.waitForRevertButtonVanish()) {
        throw new ProcessException("Mod screen: revert button has not vanished. Aborting.");
      }

      if (allModsOk) {
        LOG.info("End of processing character {}: complete", characterName);
        moveStatus.getDone().add(characterName);
        moveStatus.getAttention().remove(characterName);
        moveStatus.getToProcess().remove(characterName);
      }
      else {
        LOG.warn("End of processing character {}: may be incomplete, attention is required", characterName);
        moveStatus.getAttention().add(characterName);
      }
      JsonConnector.writeObjectToFile(moveStatus, moveStatusFile);

      ModScreen.exitModScreen();
      AutomationUtil.waitFor(2000L);
    }

    LOG.info("Finished");
  }

  private ModProcessResult processMod(Mod mod) {
    LOG.info("Processing mod: {}", mod);
    AutomationUtil.waitFor(250L);

    // Check if the required mod is already assigned
    AutomationUtil.click(ModScreen.LM_CHAR_MOD_MAP.get(mod.getSlot()), "Clicking on character mod with slot " + mod.getSlot());
    if (!ModScreen.waitForMinusButton()) {
      throw new ProcessException("Mod screen: minus button not found. Aborting.");
    }
    AutomationUtil.waitFor(250L);
    if (!ModScreen.checkForUnassignedLabel() && ModScreen.matchMods(mod, true)) {
      return ModProcessResult.ALREADY_ASSIGNED;
    }

    // Filter the mod
    boolean ok = false;
    for (int i = 0; i < 3; i++) {
      ModScreen.enterModFilter();
      if (!ModScreenFilter.waitForTitle()) {
        throw new ProcessException("Mod screen filter: title not found. Aborting.");
      }
      ModScreenFilter.clickDefaultAndEnsureAnySlotIsOnTop();
      try {
        ModScreenFilter.filterForMod(mod);
      }
      catch (ProcessException e) {
        ModScreenFilter.clickDefault();
        ModScreenFilter.closeWithoutConfirm();
        continue;
      }
      ModScreenFilter.confirm();
      ok = true;
      break;
    }
    if (!ok) {
      throw new ProcessException("Mod screen filter: could not locate the secondary stats label after 3 attempts. Aborting.");
    }
    AutomationUtil.waitFor(750L);
    if (!ModScreen.waitForSortButton()) {
      throw new ProcessException("Mod screen: buttons not found. Aborting.");
    }

    // Iterate through the mods
    boolean foundMod = false;
    int foundIndex = -1;
    for (Integer index : ModScreen.readOtherModLocations()) {
      if (ModScreen.matchMods(mod, false)) {
        foundMod = true;
        foundIndex = index;
        break;
      }
    }

    if (foundMod) {
      LOG.info("Mod found at index {}", foundIndex);
      AutomationUtil.click(ModScreen.LL_OTHER_MODS.get(foundIndex), "Clicking again on found mod at index " + foundIndex + " to assign it");
      AutomationUtil.waitFor(750L);
      ModScreen.StateAfterModMoveOrder status = ModScreen.waitAndGetStateAfterModMoveOrder();
      switch (status) {
        case NONE:
          throw new ProcessException("Could not assign the mod; aborting");
        case ASSIGN_LOADOUT_BUTTON:
          AutomationUtil.click(ModScreen.R_ASSIGN_LOADOUT_BUTTON.getCenter(), "Clicking on assign button");
          break;
        case REMOVE_BUTTON:
          AutomationUtil.click(ModScreen.R_REMOVE_BUTTON.getCenter(), "Clicking on remove button");
          break;
        case FILTER_AND_SORT_BUTTONS:
          LOG.info("Mod has been assigned without dialog box");
      }
      return ModProcessResult.FOUND_AND_ASSIGNED;
    }
    else {
      LOG.info("Mod not found!!");
      return ModProcessResult.NOT_FOUND;
    }
  }

}
