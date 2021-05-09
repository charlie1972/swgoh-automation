package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TakeScreenshot extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshot.class);

  @Override
  protected void doProcess() {
    String file = AutomationUtil.takeScreenshot("D:\\Temp");
    LOG.info("Screenshot taken at: {}", file);
  }

}
