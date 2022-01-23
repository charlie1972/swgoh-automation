package com.charlie.swgoh.window;

public class BlueStacks4Window extends BlueStacksWindow {

  private static final String CLASS_NAME = "HWNDWRAPPER[BLUESTACKS.EXE";

  @Override
  protected String getClassNameForBluestacks() {
    return CLASS_NAME;
  }

  @Override
  protected String getClassNameForViewport() {
    return CLASS_NAME;
  }

  @Override
  protected String getWindowTextForBluestacks() {
    return "BlueStacks";
  }

  @Override
  protected String getWindowTextForViewport() {
    return "KeymapCanvasWindow";
  }

}
