package com.charlie.swgoh.automation;

public enum FeetbackStatus {

  IDLE("Idle"),
  RUNNING("Running... Press Ctrl-Shift-Space to pause. Press Ctrl-Shift-Q to stop."),
  PAUSING("Pause signal received. Please wait..."),
  PAUSED("Paused. Press Ctrl-Shift-Space to unpause. Press Ctrl-Shift-Q to stop."),
  STOPPING("Stop signal received. Please wait...");

  private final String text;

  FeetbackStatus(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}
