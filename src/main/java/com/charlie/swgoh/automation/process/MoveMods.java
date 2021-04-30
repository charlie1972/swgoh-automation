package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.CharacterModsScreen;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.FileUtil;
import com.charlie.swgoh.util.ModUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MoveMods extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(MoveMods.class);

  private String fileName;

  private enum ModProcessResult {
    ALREADY_ASSIGNED,
    FOUND_AND_ASSIGNED,
    NOT_FOUND
  }

  @Override
  public void setParameters(String... parameters) {
    fileName = parameters[0];
  }

  @Override
  public void init() {
  }

  @Override
  protected void doProcess() throws Exception {
    LOG.info("Starting moving mods");

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);
    String reportFile = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "report-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()),
            "txt"
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

    if (!CharacterModsScreen.waitForCharacterModsTitle()) {
      throw new ProcessException("Character mods screen: title text not found. Aborting.");
    }

    Map<String, List<Mod>> modMap = HtmlConnector.getModsByCharacterFromHTML(fileName);
    FileUtil.deleteFileIfExists(attentionCharactersFile);
    List<String> alreadyProcessedCharacters = FileUtil.readFromFile(processedCharactersFile);
    LOG.info("Characters already processed: {}", alreadyProcessedCharacters);
    alreadyProcessedCharacters.forEach(modMap.keySet()::remove);

    int numberOfCharactersToProcess = modMap.size();
    int numberOfProcessedCharacters = 0;
    for (Map.Entry<String, List<Mod>> entry : modMap.entrySet()) {
      handleKeys();

      if (!CharacterModsScreen.waitForCharacterModsTitle()) {
        throw new ProcessException("Character mods screen: title text not found. Aborting.");
      }

      numberOfProcessedCharacters++;
      double progress = (double)numberOfProcessedCharacters / (double)numberOfCharactersToProcess;
      setProgress(progress);

      String characterName = entry.getKey();
      FileUtil.writeToFile(reportFile, "Character: " + characterName);
      String message = "Processing character: " + characterName;
      LOG.info(message);
      setMessage(message);

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

      ModScreen.dragOtherModsToTop();

      boolean allModsOk = true;
      for (Mod mod : entry.getValue()) {
        ModProcessResult result = processMod(mod);
        AutomationUtil.waitFor(500L);
        LOG.info("Process mod: {}", result);
        if (!ModScreen.waitForFilterAndSortButtons()) {
          throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
        }
        allModsOk = allModsOk && (result == ModProcessResult.ALREADY_ASSIGNED || result == ModProcessResult.FOUND_AND_ASSIGNED);
        FileUtil.writeToFile(reportFile, mod.toString() + " => " + result);
      }

      AutomationUtil.waitFor(750L);
      // If we are in a state where the last mod is not found, we have to close the mod stats first
      if (ModScreen.checkForMinusButton()) {
        AutomationUtil.click(ModScreen.R_MINUS_BUTTON, "Clicking on minus button");
        AutomationUtil.waitFor(1000L);
      }
      if (ModScreen.checkForRevertButton()) {
        AutomationUtil.click(ModScreen.L_CONFIRM_BUTTON, "Clicking on confirm button");
        AutomationUtil.waitFor(1000L);
      }
      else {
        throw new ProcessException("Mod screen: confirm button not found. Aborting.");
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
    if (!ModScreen.checkForUnassignedLabel() && ModUtil.matchMods(mod, ModScreen.extractModText(true))) {
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
    boolean foundMod = false;
    int foundModIndex = -1;
    int modCount = ModScreen.countModsFromDots();
    if (modCount != 0) {
      LOG.info("Number of mods found after filter: {}", modCount);
    }
    else {
      LOG.error("No mod after filter!!");
    }
    for (int i = 0; i < modCount; i++) {
      AutomationUtil.click(ModScreen.LL_OTHER_MODS.get(i), "Clicking on mod #" + i);
      if (!ModScreen.waitForMinusButton()) {
        throw new ProcessException("Mod screen: minus button not found. Aborting.");
      }
      AutomationUtil.waitFor(250L);
      if (ModUtil.matchMods(mod, ModScreen.extractModText(false))) {
        foundMod = true;
        foundModIndex = i;
        break;
      }
    }
    if (foundMod) {
      LOG.info("Mod found in index {}", foundModIndex);
      AutomationUtil.click(ModScreen.LL_OTHER_MODS.get(foundModIndex), "Clicking again on found mod #" + foundModIndex + " to assign it");
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
