package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.screen.ModScreen;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProcess extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(TestProcess.class);

  @Override
  public void init() {
    BlueStacksApp.showAndAdjust();
    ModScreen.init();
  }

  @Override
  protected void doProcess() throws Exception {
    for (Region region : ModScreen.getRegModDots()) {
      region.highlight(3d);
    }



  }

}
