package com.charlie.swgoh.automation;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

import java.io.*;
import java.util.Properties;

public class Configuration {

  private static final String PROPERTIES_FILE_NAME = "swgoh-automation.properties";
  private static final String DEFAULT_DIRECTORY = "defaultDirectory";
  private static final String SPEED = "speed";
  private static final String WINDOW_X = "windowX";
  private static final String WINDOW_Y = "windowY";

  private static String defaultDirectory;
  private static AutomationSpeed speed;
  private static Double windowX;
  private static Double windowY;

  private static IFeedback feedback;

  private Configuration() {}

  public static void configure() {
    Debug.off();
    Settings.ActionLogs = false;
    Settings.InfoLogs = false;
    Settings.DebugLogs = false;
    Settings.MoveMouseDelay = 0.1F * (float)Configuration.getSpeed().getDelayMultiplier();

    Key.removeHotkey('q', KeyModifier.CTRL + KeyModifier.SHIFT);
    Key.addHotkey('q', KeyModifier.CTRL + KeyModifier.SHIFT, new StopAppKeyHandler(feedback));
    Key.removeHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT);
    Key.addHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT, new PauseAppKeyHandler(feedback));

    ImagePath.add("com.charlie.swgoh.main.FXApp/images");
  }

  public static void setFeedback(IFeedback feedback) {
    Configuration.feedback = feedback;
  }

  public static String getDefaultDirectory() {
    return defaultDirectory;
  }

  public static void setDefaultDirectory(String defaultDirectory) {
    Configuration.defaultDirectory = defaultDirectory;
  }

  public static AutomationSpeed getSpeed() {
    return speed;
  }

  public static void setSpeed(AutomationSpeed speed) {
    Configuration.speed = speed;
  }

  public static Double getWindowX() {
    return windowX;
  }

  public static void setWindowX(Double windowX) {
    Configuration.windowX = windowX;
  }

  public static Double getWindowY() {
    return windowY;
  }

  public static void setWindowY(Double windowY) {
    Configuration.windowY = windowY;
  }

  public static void loadProperties() {
    File propertiesFile = new File(PROPERTIES_FILE_NAME);
    if (propertiesFile.exists()) {
      Properties properties = new Properties();
      try (InputStream inputStream = new FileInputStream(PROPERTIES_FILE_NAME)) {
        properties.load(inputStream);
      } catch (IOException e) {
        feedback.setErrorMessage("Load properties: " + e.getMessage());
      }
      defaultDirectory = properties.getProperty(DEFAULT_DIRECTORY, "");
      speed = properties.getProperty(SPEED) != null ? AutomationSpeed.fromText(properties.getProperty(SPEED)) : AutomationSpeed.FASTEST;
      try {
        windowX = Double.parseDouble(properties.getProperty(WINDOW_X));
        windowY = Double.parseDouble(properties.getProperty(WINDOW_Y));
      }
      catch (NumberFormatException | NullPointerException e) {
        windowX = null;
        windowY = null;
      }
    }
    else {
      defaultDirectory = "";
      speed = AutomationSpeed.FASTEST;
      windowX = null;
      windowY = null;
    }
  }

  public static void saveProperties() {
    Properties properties = new Properties();
    properties.setProperty(DEFAULT_DIRECTORY, defaultDirectory);
    properties.setProperty(SPEED, speed.getText());
    properties.setProperty(WINDOW_X, String.valueOf(windowX));
    properties.setProperty(WINDOW_Y, String.valueOf(windowY));
    try (OutputStream outputStream = new FileOutputStream(PROPERTIES_FILE_NAME)) {
      properties.store(outputStream, "Configuration for SWGoH Automation");
    }
    catch (IOException e) {
      feedback.setErrorMessage("Save properties: " + e.getMessage());
    }
  }

}
