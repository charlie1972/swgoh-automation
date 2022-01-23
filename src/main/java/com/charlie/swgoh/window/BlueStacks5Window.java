package com.charlie.swgoh.window;

public class BlueStacks5Window extends BlueStacksWindow {

  @Override
  protected String getClassNameForBluestacks() {
    return "QT5154QWINDOWOWNDCICON";
  }

  @Override
  protected String getClassNameForViewport() {
    return "QT5154QWINDOWTOOLSAVEBITSOWNDC";
  }

  @Override
  protected String getWindowTextForBluestacks() {
    return "BlueStacks";
  }

  @Override
  protected String getWindowTextForViewport() {
    return "BlueStacks Keymap Overlay";
  }

}
