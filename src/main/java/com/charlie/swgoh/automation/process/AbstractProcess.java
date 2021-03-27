package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.AppKeyHolder;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProcess implements IProcess {

  private IFeedback feedback = null;
  private final Logger LOG;

  public AbstractProcess() {
    LOG = LoggerFactory.getLogger(this.getClass());
  }

  public void setFeedback(IFeedback feedback) {
    this.feedback = feedback;
  }

  @Override
  public void process() {
    Configuration.configure();
    init();
    feedbackRunning();
    try {
      doProcess();
      feedbackFinished();
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
    }
    finally {
      feedbackIdle();
    }
  }

  protected abstract void doProcess() throws Exception;

  protected void setStatus(String status) {
    if (feedback != null) {
      feedback.setStatus(status);
    }
  }

  protected void setMessage(String message) {
    if (feedback != null) {
      feedback.setMessage(message);
    }
  }

  protected void setErrorMessage(String errorMessage) {
    if (feedback != null) {
      feedback.setErrorMessage(errorMessage);
    }
  }

  protected void setProgress(double progress) {
    if (feedback != null) {
      feedback.setProgress(progress);
    }
  }

  protected void setAllControlsDisabled(boolean disabled) {
    if (feedback != null) {
      feedback.setAllControlsDisabled(disabled);
    }
  }

  private void feedbackRunning() {
    setAllControlsDisabled(true);
    setStatus("Running... Press Ctrl-Shift-Space to pause. Press Strl-Shift-Q to stop.");
  }

  private void feedbackPause() {
    setStatus("Paused. Press Ctrl-Shift-Space to unpause. Press Strl-Shift-Q to stop.");
  }

  private void feedbackFinished() {
    setMessage("Finished");
  }

  private void feedbackIdle() {
    setStatus("Idle");
    setAllControlsDisabled(false);
  }

  protected void handleKeys() {
    handleStop();
    boolean first = true;
    boolean hasPaused = false;
    while (AppKeyHolder.isPaused) {
      hasPaused = true;
      if (first) {
        LOG.info("*** Paused ***");
        feedbackPause();
        first = false;
      }
      handleStop();
      AutomationUtil.waitForDelay();
    }
    if (hasPaused) {
      init();
      feedbackRunning();
    }
  }

  private void handleStop() {
    if (AppKeyHolder.isStopping) {
      AppKeyHolder.isStopping = false;
      throw new ProcessException("Interrupted");
    }
  }

}
