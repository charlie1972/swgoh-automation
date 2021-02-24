package com.charlie.swgoh.automation;

import org.sikuli.script.App;
import org.sikuli.script.Region;

public class BlueStacksApp {

  private BlueStacksApp() {}

  private static App app = null;
  private static Region window = null;

  public static void focus() {
    app = App.focus("BlueStacks");
  }

  public static App getApp() {
    if (app == null) {
      focus();
    }
    return app;
  }

  public static Region getWindow() {
    return window != null ? window : getApp().window();
  }

  public static void lockWindow() {
    window = getApp().window();
  }

}
