package com.charlie.swgoh.automation;

import com.charlie.swgoh.automation.process.AbstractProcess;
import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseAppKeyHandler extends HotkeyListener {

  private static final Logger LOG = LoggerFactory.getLogger(PauseAppKeyHandler.class);

  private IFeedback feedback = null;

  public PauseAppKeyHandler(IFeedback feedback) {
    this.feedback = feedback;
  }

  @Override
  public void hotkeyPressed(HotkeyEvent hotkeyEvent) {
    if (!AppKeyHolder.isPaused) {
      LOG.info("*************************");
      LOG.info("* Pause signal received *");
      LOG.info("*************************");
      if (feedback != null) {
        feedback.setStatus("Pause signal received. Please wait...");
      }
      AppKeyHolder.isPaused = true;
    }
    else {
      LOG.info("***************************");
      LOG.info("* Unpause signal received *");
      LOG.info("***************************");
      if (feedback != null) {
        feedback.setStatus(AbstractProcess.MESSAGE_RUNNING);
      }
      AppKeyHolder.isPaused = false;
    }
  }

}
