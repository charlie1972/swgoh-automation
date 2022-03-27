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

import java.time.Duration;

public abstract class AbstractProcess {

  private IFeedback feedback = null;
  private final Logger LOG;

  protected double progress;
  protected double startProgress;
  protected long startTimeMillis;

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
      progress = 0.0;
      startProgress = 0.0;
      startTimeMillis = System.currentTimeMillis();
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

  private void setProgress() {
    if (feedback != null) {
      feedback.setProgress(progress);
    }
  }

  private void setETA(String eta) {
    if (feedback != null) {
      feedback.setETA(eta);
    }
  }

  protected void setAllControlsDisabled(boolean disabled) {
    if (feedback != null) {
      feedback.setAllControlsDisabled(disabled);
    }
  }

  protected void updateProgressAndETA() {
    setProgress();

    double elapsedProgress = progress - startProgress;
    if (elapsedProgress < 0.01) {
      setETA("Computing...");
      return;
    }

    long timeElapsedMillis = System.currentTimeMillis() - startTimeMillis;
    double rate = timeElapsedMillis / elapsedProgress;
    long etaMillis = (long) ((1.0 - progress) * rate);
    Duration duration = Duration.ofMillis(etaMillis);
    int hours = duration.toHoursPart();
    int minutes = duration.toMinutesPart();
    int seconds = duration.toSecondsPart();
    StringBuilder sb = new StringBuilder();
    if (hours > 0) {
      sb.append(hours).append("h ");
    }
    if (minutes > 0) {
      sb.append(minutes).append("m ");
    }
    if (seconds > 0) {
      sb.append(seconds).append("s");
    }
    setETA(sb.toString());
  }

  private void feedbackRunning() {
    setAllControlsDisabled(true);
    setStatus(FeedbackStatus.RUNNING);
  }

  private void feedbackPause() {
    setStatus(FeedbackStatus.PAUSED);
    setETA("Paused");
  }

  private void feedbackFinished() {
    setMessage("Finished");
    progress = 0.0;
    setProgress();
    setETA("Finished");
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
      startProgress = progress;
      startTimeMillis = System.currentTimeMillis();
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
