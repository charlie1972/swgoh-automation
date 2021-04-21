package com.charlie.swgoh.automation;

import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseAppKeyHandler extends HotkeyListener {

  private static final Logger LOG = LoggerFactory.getLogger(PauseAppKeyHandler.class);

  private final IFeedback feedback;

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
        feedback.setStatus(FeedbackStatus.PAUSING);
      }
      AppKeyHolder.isPaused = true;
    }
    else {
      LOG.info("***************************");
      LOG.info("* Unpause signal received *");
      LOG.info("***************************");
      if (feedback != null) {
        feedback.setStatus(FeedbackStatus.RUNNING);
      }
      AppKeyHolder.isPaused = false;
    }
  }

}
