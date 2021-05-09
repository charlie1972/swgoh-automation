package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveMods extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(MoveMods.class);

  private String fileName;
  private boolean isDryRun;

  private enum ModProcessResult {
    ALREADY_ASSIGNED,
    FOUND_AND_ASSIGNED,
    NOT_FOUND
  }

  @Override
  public void setParameters(String... parameters) {
    fileName = parameters[0];
    isDryRun = Boolean.parseBoolean(parameters[1]);
  }

  @Override
  protected void doProcess() throws Exception {
    LOG.info("Starting moving mods");
    if (isDryRun) {
      LOG.info("DRY RUN");
    }
    else {
      LOG.info("LIVE RUN");
      for (int countdown = 10; countdown > 0; countdown--) {
        setMessage("WARNING: LIVE RUN. If you wish to abort, type Ctrl-Shift-Q. Starting in " + countdown + " second" + (countdown > 1 ? "s" : ""));
        AutomationUtil.waitForFixed(1000L);
        handleKeys();
      }
    }

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);
    String reportFile = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "report-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()),
            "csv"
    ).toString();
    String processedCharactersFile = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "processedCharacters",
            "txt"
    ).toString();
    String attentionCharactersFile = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "attentionCharacters",
            "txt"
    ).toString();

    FileUtil.deleteFileIfExists(attentionCharactersFile);
    FileUtil.deleteFileIfExists(reportFile);

    Map<String, List<Mod>> modMap = HtmlConnector.getModsByCharacterFromHTML(fileName);
    List<String> alreadyProcessedCharacters = FileUtil.readFromFile(processedCharactersFile);
    LOG.info("Characters already processed: {}", alreadyProcessedCharacters);
    alreadyProcessedCharacters.forEach(modMap.keySet()::remove);

    if (!CharacterModsScreen.waitForCharacterModsTitle()) {
      throw new ProcessException("Character mods screen: title text not found. Aborting.");
    }

    FileUtil.writeToFile(
            reportFile,
            "Character name;Result;Slot;Set;Dots;Tier;Primary stat;Secondary stat 1;Secondary stat 2;Secondary stat 3;Secondary stat 4"
    );

    int numberOfCharactersToProcess = modMap.size();
    int numberOfProcessedCharacters = 0;
    for (Map.Entry<String, List<Mod>> entry : modMap.entrySet()) {
      handleKeys();

      String characterName = entry.getKey();
      String message = "Character: " + characterName;
      LOG.info(message);
      setMessage(message);

      if (!CharacterModsScreen.waitForCharacterModsTitle()) {
        throw new ProcessException("Character mods screen: title text not found. Aborting.");
      }

      numberOfProcessedCharacters++;
      double progress = (double)numberOfProcessedCharacters / (double)numberOfCharactersToProcess;
      setProgress(progress);

      AutomationUtil.waitFor(250L);
      CharacterModsScreen.filterName(characterName);
      AutomationUtil.waitFor(250L);
      CharacterModsScreen.enterModScreen(characterName);
      if (!ModScreen.waitForFilterAndSortButtons()) {
        throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
      }
      if (!ModScreen.checkName(characterName)) {
        throw new ProcessException("Mod screen: name on screen doesn't match " + characterName + ". Aborting.");
      }

      boolean allModsOk = true;
      for (Mod mod : entry.getValue()) {
        String message2 = "Character: " + characterName + ", Slot: " + mod.getSlot();
        LOG.info(message2);
        setMessage(message2);

        ModProcessResult result = processMod(mod);
        LOG.info("Process mod: {}", result);
        AutomationUtil.waitFor(500L);
        if (!ModScreen.waitForFilterAndSortButtons()) {
          throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
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

      if (!ModScreen.waitForFilterAndSortButtons()) {
        throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
      }

      if (allModsOk) {
        LOG.info("End of processing character {}: complete", characterName);
        FileUtil.writeToFile(processedCharactersFile, characterName);
      }
      else {
        LOG.info("End of processing character {}: may be incomplete, attention is required", characterName);
        FileUtil.writeToFile(attentionCharactersFile, characterName);
      }

      ModScreen.exitModScreen();
      AutomationUtil.waitFor(1500L);
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
    ModScreen.enterModFilter();
    if (!ModScreenFilter.waitForTitle()) {
      throw new ProcessException("Mod screen filter: title not found. Aborting.");
    }
    ModScreenFilter.ensureUnassignedIsUnchecked();
    ModScreenFilter.filterForMod(mod);
    ModScreenFilter.confirm();
    AutomationUtil.waitFor(750L);
    if (!ModScreen.waitForFilterAndSortButtons()) {
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
      if (status == ModScreen.StateAfterModMoveOrder.NONE) {
        throw new ProcessException("Could not assign the mod; aborting");
      }
      if (status == ModScreen.StateAfterModMoveOrder.ASSIGN_LOADOUT_BUTTON) {
        AutomationUtil.click(ModScreen.R_ASSIGN_LOADOUT_BUTTON.getCenter(), "Clicking on assign button");
      }
      if (status == ModScreen.StateAfterModMoveOrder.REMOVE_BUTTON) {
        AutomationUtil.click(ModScreen.R_REMOVE_BUTTON.getCenter(), "Clicking on remove button");
      }
      // Last case is the mod has been assigned without dialog box, returning immediately
      LOG.info("Mod has been assigned without dialog box");
      return ModProcessResult.FOUND_AND_ASSIGNED;
    }
    else {
      LOG.info("Mod not found!!");
      return ModProcessResult.NOT_FOUND;
    }
  }

}
