package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.CharacterModsScreen;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.FileUtil;
import com.charlie.swgoh.util.ModUtil;
import org.sikuli.script.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReadUnequippedMods extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(ReadUnequippedMods.class);

  private String allyCode;
  private String fileName;

  @Override
  public void setParameters(String... parameters) {
    allyCode = parameters[0];
    fileName = parameters[1];
  }

  @Override
  public void init() {
  }

  @Override
  protected void doProcess() throws Exception {
    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);
    String resultFileName = new FileUtil.FileComponents(
            fileComponents.getDirectoryName(),
            "enriched-" + fileComponents.getFileName(),
            fileComponents.getExtension()
    ).toString();

    Progress progress = JsonConnector.readProgressFromFile(fileName);
    Optional<Profile> optProfile = progress.getProfiles().stream().filter(profile -> allyCode.equals(profile.getAllyCode())).findFirst();
    if (optProfile.isEmpty()) {
      throw new ProcessException("Profile with ally code " + allyCode + " not found.");
    }
    Profile profile = optProfile.get();
    List<com.charlie.swgoh.datamodel.json.Mod> assignedMods = profile.getMods().stream().filter(mod -> mod.getCharacterID() != null).collect(Collectors.toList());
    profile.setMods(assignedMods);

    handleKeys();

    if (!ModScreen.waitForFilterAndSortButtons()) {
      throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
    }
    String message = "Reading mods";
    LOG.info(message);
    setMessage(message);

    ModScreen.enterModFilter();
    if (!ModScreenFilter.waitForTitle()) {
      throw new ProcessException("Mod screen filter: title not found. Aborting.");
    }
    ModScreenFilter.ensureUnassignedIsChecked();
    ModScreenFilter.clickDefault();
    ModScreenFilter.confirm();
    AutomationUtil.waitFor(1000L);
    if (!ModScreen.waitForFilterAndSortButtons()) {
      throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
    }
    ModScreen.dragOtherModsToTop();
    AutomationUtil.waitFor(1000L);

    int startModIndex = 0;
    int modNumber = 0;
    boolean isContinue;
    do {
      int modCount = ModScreen.countModsFromDots();
      LOG.info("Visible mod count: {}", modCount);
      setProgress(ModScreen.computeModProgress());
      for (int i = startModIndex; i < modCount; i++) {
        handleKeys();

        Location loc = ModScreen.LL_OTHER_MODS.get(i);
        try {
          modNumber++;
          setMessage("Reading mod #" + modNumber);
          Mod mod = readOtherModAtLocation(loc);
          if (mod == null) {
            continue;
          }
          LOG.info("Read mod: {}", mod);
          com.charlie.swgoh.datamodel.json.Mod jsonMod = ModUtil.convertToJsonMod(mod);
          profile.getMods().add(jsonMod);
        }
        catch (RuntimeException e) {
          LOG.warn("Could not read mod for location #{}. {}: {}", i, e.getClass().getName(), e.getMessage());
          String file = AutomationUtil.takeScreenshot(fileComponents.getDirectoryName());
          LOG.warn("Screenshot taken: {}", file);
        }
      }
      if (modCount < 16) {
        isContinue = false;
      }
      else {
        startModIndex = 12;
        isContinue = ModScreen.dragOtherModsListOneLineUp();
      }
    }
    while (isContinue);

    JsonConnector.writeProgressToFile(progress, resultFileName);

    LOG.info("Finished");
  }

  private Mod readOtherModAtLocation(Location loc) {
    AutomationUtil.click(loc, "Clicking on other mod");
    AutomationUtil.waitFor(250L);
    if (!ModScreen.waitForMinusButton()) {
      throw new ProcessException("Mod screen: minus button not found. Aborting.");
    }

    int level = ModScreen.extractOtherModLevel();
    if (level < 15) {
      return null;
    }

    ModScreen.DotsTierSetAndSlot dotsTierSetAndSlot = ModScreen.extractOtherModDotsTierSetAndSlot();
    if (dotsTierSetAndSlot == null || dotsTierSetAndSlot.getDots() < 5) {
      return null;
    }

    Mod mod = ModScreen.extractModStats(false);
    mod.setCharacter(null);
    mod.setSlot(dotsTierSetAndSlot.getSlot());
    mod.setSet(dotsTierSetAndSlot.getSet());
    mod.setDots(dotsTierSetAndSlot.getDots());
    mod.setLevel(level);
    mod.setTier(dotsTierSetAndSlot.getTier());
    return mod;
  }

}
