package com.charlie.swgoh.automation;

public enum AutomationSpeed {

  FASTEST("Fastest", 1d),
  FAST("Fast", 1.2d),
  MEDIUM("Medium", 1.5d),
  SLOWEST("Slowest", 2d);

  private final String text;
  private final double delayMultiplier;

  AutomationSpeed(String text, double delayMultiplier) {
    this.text = text;
    this.delayMultiplier = delayMultiplier;
  }

  public String getText() {
    return text;
  }

  public double getDelayMultiplier() {
    return delayMultiplier;
  }

  @Override
  public String toString() {
    return text;
  }

  public static AutomationSpeed fromText(String text) {
    for (AutomationSpeed automationSpeed : AutomationSpeed.values()) {
      if (automationSpeed.text.equals(text)) {
        return automationSpeed;
      }
    }
    return null;
  }

}
