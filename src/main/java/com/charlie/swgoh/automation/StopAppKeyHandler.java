package com.charlie.swgoh.automation;

import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopAppKeyHandler extends HotkeyListener {

  private static final Logger LOG = LoggerFactory.getLogger(StopAppKeyHandler.class);

  @Override
  public void hotkeyPressed(HotkeyEvent hotkeyEvent) {
    LOG.info("************************");
    LOG.info("* Stop signal received *");
    LOG.info("************************");
    AppKeyHolder.isStopping = true;
  }

}
