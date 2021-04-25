package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.screen.ModScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProcess extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(TestProcess.class);

  @Override
  public void init() {
    ModScreen.init();
  }

  @Override
  protected void doProcess() throws Exception {
    ModScreen.computeModProgress();
  }

}
