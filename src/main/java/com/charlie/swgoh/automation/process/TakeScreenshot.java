package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TakeScreenshot implements IProcess{

  private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshot.class);

  @Override
  public void init() {

  }

  @Override
  public void process() {
    init();

    String file = AutomationUtil.takeScreenshot("D:\\Temp");
    LOG.info("Screenshot taken at: {}", file);
  }

}
