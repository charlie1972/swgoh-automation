package com.charlie.swgoh.automation;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

public class Configuration {

  private Configuration() {}

  public static void configure() {
    Debug.off();
    Settings.ActionLogs = false;
    Settings.InfoLogs = false;
    Settings.DebugLogs = false;
    Settings.MoveMouseDelay = 0.1F;

    Key.addHotkey('q', KeyModifier.CTRL + KeyModifier.SHIFT, new StopAppKeyHandler());
    Key.addHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT, new PauseAppKeyHandler());

    ImagePath.add("com.charlie.swgoh.main.FXMain/images");
  }

}
