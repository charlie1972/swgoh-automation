package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
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
    BlueStacksApp.showAndAdjust();
    CharacterModsScreen.init();
    ModScreen.init();
    ModScreenFilter.init();
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
    List<com.charlie.swgoh.datamodel.json.Mod> newMods = profile.getMods().stream().filter(mod -> mod.getCharacterID() != null).collect(Collectors.toList());
    profile.setMods(newMods);

    int totalNumber = ModSlot.values().length * ModSet.values().length;
    int currentNumber = 0;

    for (ModSlot slot : ModSlot.values()) {
      for (ModSet set : ModSet.values()) {
        handleKeys();

        if (!ModScreen.waitForFilterAndSortButtons()) {
          throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
        }
        String message = "Reading mods with slot: " + slot + " and set: " + set;
        LOG.info(message);
        setMessage("Reading mods with slot: " + slot + " and set: " + set);
        currentNumber++;
        double progressDouble = (double)currentNumber / (double)totalNumber;
        setProgress(progressDouble);

        ModScreen.enterModFilter();
        if (!ModScreenFilter.waitForTitle()) {
          throw new ProcessException("Mod screen filter: title not found. Aborting.");
        }
        ModScreenFilter.ensureUnassignedIsChecked();
        ModScreenFilter.filterForModSlotAndSet(slot, set);
        ModScreenFilter.confirm();
        if (!ModScreen.waitForFilterAndSortButtons()) {
          throw new ProcessException("Mod screen: filter and sort buttons not found. Aborting.");
        }
        AutomationUtil.waitFor(500L);
        int modCount = ModScreen.countModsFromDots();
        LOG.info("Mod count: {}", modCount);
        for (int i = 0; i < modCount; i++) {
          handleKeys();

          Location loc = ModScreen.getLocOtherMods().get(i);
          try {
            Mod mod = readOtherModAtLocation(slot, set, loc);
            if (mod == null) {
              continue;
            }
            LOG.info("Read mod: {}", mod.toString());
            com.charlie.swgoh.datamodel.json.Mod jsonMod = ModUtil.convertToJsonMod(mod);
            profile.getMods().add(jsonMod);
          }
          catch (RuntimeException e) {
            LOG.warn("Could not read mod for slot {}, set {}, at location #{}. {}: {}", slot, set, i, e.getClass().getName(), e.getMessage());
            String file = AutomationUtil.takeScreenshot(fileComponents.getDirectoryName());
            LOG.warn("Screenshot taken: {}", file);
          }
        }
      }
    }

    JsonConnector.writeProgressToFile(progress, resultFileName);

    LOG.info("Finished");
  }

  private Mod readOtherModAtLocation(ModSlot slot, ModSet set, Location loc) {
    AutomationUtil.click(loc, "Clicking on other mod");
    AutomationUtil.waitFor(250L);
    if (!ModScreen.waitForMinusButton()) {
      throw new ProcessException("Mod screen: minus button not found. Aborting.");
    }

    ModScreen.LevelAndTier levelAndTier = ModScreen.extractOtherModLevelAndTier();
    if (levelAndTier.getLevel() < 15) {
      return null;
    }
    int dots = ModScreen.extractOtherModDots();
    if (dots < 5) {
      return null;
    }

    Mod mod = ModScreen.extractModStats(false);
    mod.setCharacter(null);
    mod.setSlot(slot);
    mod.setSet(set);
    mod.setDots(dots);
    mod.setLevel(levelAndTier.getLevel());
    mod.setTier(levelAndTier.getTier());
    return mod;
  }

}
