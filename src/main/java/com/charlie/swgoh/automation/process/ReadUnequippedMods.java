package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.FileUtil;
import com.charlie.swgoh.util.ModUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ReadUnequippedMods extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(ReadUnequippedMods.class);

  private final String allyCode;
  private final String fileName;

  public ReadUnequippedMods(String allyCode, String fileName) {
    this.allyCode = allyCode;
    this.fileName = fileName;
  }

  @Override
  protected void doProcess() throws Exception {
    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);
    String resultFileName = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "enriched-" + fileComponents.getFileName(),
            fileComponents.getExtension()
    ).toString();

    Progress modOptimizerProgress = JsonConnector.readObjectFromFile(fileName, Progress.class);
    Profile profile = modOptimizerProgress.getProfiles().stream()
            .filter(p -> allyCode.equals(p.getAllyCode()))
            .findFirst()
            .orElseThrow(() -> new ProcessException("Profile with ally code " + allyCode + " not found."));
    List<com.charlie.swgoh.datamodel.json.Mod> assignedMods = profile.getMods().stream().filter(mod -> mod.getCharacterID() != null).collect(Collectors.toList());
    profile.setMods(assignedMods);

    handleKeys();

    if (!ModScreen.waitForSortButton()) {
      throw new ProcessException("Mod screen: sort button not found. Aborting.");
    }
    String message = "Reading mods";
    LOG.info(message);
    setMessage(message);

    ModScreen.enterModFilter();
    AutomationUtil.waitFor(500L);
    if (!ModScreenFilter.waitForTitle()) {
      throw new ProcessException("Mod screen filter: title not found. Aborting.");
    }
    ModScreenFilter.clickDefaultAndEnsureAnySlotIsOnTop();
    AutomationUtil.waitFor(1000L);
    ModScreenFilter.ensureUnassignedIsChecked();
    ModScreenFilter.confirm();
    AutomationUtil.waitFor(1000L);
    if (!ModScreen.waitForSortButton()) {
      throw new ProcessException("Mod screen: sort button not found. Aborting.");
    }
    ModScreen.dragOtherModsToTop();
    AutomationUtil.waitFor(1000L);

    int modNumber = 0;
    for (Integer index : ModScreen.readOtherModLocations()) {
      handleKeys();
      modNumber++;
      setMessage("Reading mod #" + modNumber);
      if (modNumber == 13) {
        startTimeMillis = System.currentTimeMillis();
      }
      if (modNumber % 4 == 1) {
        progress = ModScreen.computeModProgress();
        updateProgressAndETA();
      }
      Mod mod = null;
      try {
        mod = ModScreen.readOtherMod();
        LOG.info("Parsed mod: {}", mod);
      }
      catch (RuntimeException e) {
        LOG.warn("Error reading mod at index {}. Exception is {}: {}", index, e.getClass().getName(), e.getMessage());
      }
      if (mod == null) {
        continue;
      }
      com.charlie.swgoh.datamodel.json.Mod jsonMod = ModUtil.convertToJsonMod(mod);
      profile.getMods().add(jsonMod);
    }

    JsonConnector.writeObjectToFile(modOptimizerProgress, resultFileName);

    LOG.info("Finished");
  }

}
