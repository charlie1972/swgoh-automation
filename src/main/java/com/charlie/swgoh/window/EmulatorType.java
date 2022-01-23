package com.charlie.swgoh.window;

public enum EmulatorType {

  BLUESTACKS_4("BlueStacks 4"),
  BLUESTACKS_5("BlueStacks 5");

  private final String label;

  EmulatorType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static EmulatorType fromLabel(String label) {
    for (EmulatorType windowType : values()) {
      if (windowType.getLabel().equals(label)) {
        return windowType;
      }
    }
    return null;
  }

}
