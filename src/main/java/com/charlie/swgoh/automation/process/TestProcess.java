package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.screen.ModScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProcess implements IProcess {

  private static final Logger LOG = LoggerFactory.getLogger(TestProcess.class);

  @Override
  public void init() {
    BlueStacksApp.showAndAdjust();
    ModScreen.init();
  }

  @Override
  public void process() throws Exception {
    init();

    try {
      Mod mod = ModScreen.extractModStats(false);
      mod.setCharacter(null);
      mod.setSlot(null);
      mod.setSet(null);
      mod.setDots(ModScreen.extractOtherModDots());
      ModScreen.LevelAndTier levelAndTier = ModScreen.extractOtherModLevelAndTier();
      mod.setLevel(levelAndTier.getLevel());
      mod.setTier(levelAndTier.getTier());
      LOG.info("Read mod: {}", mod.toString());
    }
    catch (RuntimeException e) {
      LOG.warn("Could not read mod. {}: {}", e.getClass().getName(), e.getMessage());
    }

  }

}
