package com.charlie.swgoh.window;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
import org.sikuli.script.Region;

public abstract class EmulatorWindow {

  public static EmulatorWindow INSTANCE = null;

  public static void init() {
    if (INSTANCE != null) {
      return;
    }
    switch (Configuration.getEmulatorType()) {
      case BLUESTACKS_4:
        INSTANCE = new BlueStacks4Window();
        break;
      case BLUESTACKS_5:
        INSTANCE = new BlueStacks5Window();
        break;
      default:
        throw new ProcessException("Unknown emulator type: " + Configuration.getEmulatorType());
    }
  }

  public abstract void showAndAdjust();
  public abstract Region getWindow();

}
