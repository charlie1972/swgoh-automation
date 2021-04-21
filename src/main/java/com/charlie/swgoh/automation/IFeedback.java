package com.charlie.swgoh.automation;

public interface IFeedback {

  void setAllControlsDisabled(boolean disabled);

  void setStatus(FeedbackStatus status);

  void setMessage(String message);

  void setErrorMessage(String errorMessage);

  void setProgress(double progress);

}
