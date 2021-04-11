package com.charlie.swgoh.automation;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

public class Configuration {

  private IFeedback feedback;

  private Configuration() {}

  public static void configure(IFeedback feedback) {
    Debug.off();
    Settings.ActionLogs = false;
    Settings.InfoLogs = false;
    Settings.DebugLogs = false;
    Settings.MoveMouseDelay = 0.1F;

    Key.addHotkey('q', KeyModifier.CTRL + KeyModifier.SHIFT, new StopAppKeyHandler(feedback));
    Key.addHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT, new PauseAppKeyHandler(feedback));

    ImagePath.add("com.charlie.swgoh.main.FXApp/images");
  }

}
