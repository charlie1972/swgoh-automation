package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.AppKeyHolder;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.FeedbackStatus;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.window.EmulatorWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProcess {

  private IFeedback feedback = null;
  private final Logger LOG;

  public AbstractProcess() {
    LOG = LoggerFactory.getLogger(this.getClass());
  }

  public void setParameters(String... parameters) {}

  public void setFeedback(IFeedback feedback) {
    this.feedback = feedback;
  }

  public void process() {
    try {
      AutomationUtil.waitForDelay();
      Configuration.configure();
      EmulatorWindow.init();
      EmulatorWindow.INSTANCE.showAndAdjust();
      setMessage("");
      feedbackRunning();
      doProcess();
      feedbackFinished();
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
    }
    finally {
      feedbackIdle();
      setProgress(0d);
    }
  }

  protected abstract void doProcess() throws Exception;

  protected void setStatus(FeedbackStatus status) {
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
    setStatus(FeedbackStatus.RUNNING);
  }

  private void feedbackPause() {
    setStatus(FeedbackStatus.PAUSED);
  }

  private void feedbackFinished() {
    setMessage("Finished");
  }

  private void feedbackIdle() {
    setStatus(FeedbackStatus.IDLE);
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
      EmulatorWindow.INSTANCE.showAndAdjust();
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
