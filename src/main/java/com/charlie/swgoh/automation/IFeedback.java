package com.charlie.swgoh.automation;

import javafx.application.Platform;

public interface IFeedback {

  void setAllControlsDisabled(boolean disabled);

  void setStatus(String status);

  void setMessage(String message);

  void setErrorMessage(String errorMessage);

  void setProgress(double progress);

}
