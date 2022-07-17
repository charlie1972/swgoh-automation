package com.charlie.swgoh.automation;

import com.charlie.swgoh.window.EmulatorType;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.support.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Configuration {

  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

  private static final String TESSDATA_SX = "tessdataSX";
  private static final String TESSDATA_SX_SL = TESSDATA_SX + "/";

  private static final String PROPERTIES_FILE_NAME = "swgoh-automation.properties";
  private static final String DEFAULT_DIRECTORY = "defaultDirectory";
  private static final String SPEED = "speed";
  private static final String WINDOW_X = "windowX";
  private static final String WINDOW_Y = "windowY";
  private static final String EMULATOR = "emulator";

  private static final String IMAGE_PATH = Configuration.class.getName() + "/images";

  private static String defaultDirectory;
  private static AutomationSpeed speed;
  private static Double windowX;
  private static Double windowY;
  private static EmulatorType emulatorType;

  private static boolean debug = false;

  private static boolean isInitialized = false;

  private static IFeedback feedback;

  private Configuration() {}

  static {
    initTessdata();
  }

  public static void configureImagePath() {
    ImagePath.add(IMAGE_PATH);
  }

  public static void configure() {
    if (!isInitialized) {
      Debug.off();
      Settings.ActionLogs = false;
      Settings.InfoLogs = false;
      Settings.DebugLogs = false;

      Key.addHotkey('q', KeyModifier.CTRL + KeyModifier.SHIFT, new StopAppKeyHandler(feedback));
      Key.addHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT, new PauseAppKeyHandler(feedback));

      isInitialized = true;
    }
    Settings.MoveMouseDelay = 0.12F * (float) Configuration.getSpeed().getDelayMultiplier();
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

  public static EmulatorType getEmulatorType() {
    return emulatorType;
  }

  public static void setEmulatorType(EmulatorType emulatorType) {
    Configuration.emulatorType = emulatorType;
  }

  public static boolean isDebug() {
    return debug;
  }

  public static void setDebug(boolean debug) {
    Configuration.debug = debug;
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
      emulatorType = properties.getProperty(EMULATOR) != null ? EmulatorType.valueOf(properties.getProperty(EMULATOR)) : EmulatorType.BLUESTACKS_5;
      try {
        windowX = Double.parseDouble(properties.getProperty(WINDOW_X));
        windowY = Double.parseDouble(properties.getProperty(WINDOW_Y));
      }
      catch (NumberFormatException | NullPointerException e) {
        windowX = 100.0;
        windowY = 100.0;
      }
    }
    else {
      defaultDirectory = "";
      speed = AutomationSpeed.FASTEST;
      windowX = 100.0;
      windowY = 100.0;
      emulatorType = EmulatorType.BLUESTACKS_5;
    }
  }

  public static void saveProperties() {
    Properties properties = new Properties();
    properties.setProperty(DEFAULT_DIRECTORY, defaultDirectory);
    properties.setProperty(SPEED, speed.getText());
    properties.setProperty(WINDOW_X, String.valueOf(windowX));
    properties.setProperty(WINDOW_Y, String.valueOf(windowY));
    properties.setProperty(EMULATOR, emulatorType.name());
    try (OutputStream outputStream = new FileOutputStream(PROPERTIES_FILE_NAME)) {
      properties.store(outputStream, "Configuration for SWGoH Automation");
    }
    catch (IOException e) {
      feedback.setErrorMessage("Save properties: " + e.getMessage());
    }
  }

  // This method aims to fix the inability to copy the tessdata directory at OCR initialization
  private static void initTessdata() {
    // Initialize the RunTime singleton so that root directories are set, and get the tessdata directory
    File cachedTessdataDirectory = new File(RunTime.get().fSikulixAppPath, "SikulixTesseract/tessdata");
    // Check the existence of the sikuliX JAR containing the tessdata directory
    URL url = RunTime.class.getResource("/" + TESSDATA_SX);
    if (url == null || !"jar".equals(url.getProtocol())) {
      LOG.error("tessdata directory not found in sikuliX JAR, OCR will malfunction");
      return;
    }
    LOG.debug("Resource /tessdataSX found, URL is {}", url);
    // Extract name of JAR file
    String urlFile = url.getFile();
    int p = urlFile.lastIndexOf("!");
    if (!urlFile.startsWith("file:///") || p < 0) {
      LOG.error("Resource /tessdataSX is not a file in a JAR, it should start with file:/// and have ! as separator: {}", urlFile);
      return;
    }
    String jarFile = urlFile.substring(8, p);
    // Open JAR file as zip
    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jarFile))) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String zipEntryName = zipEntry.getName();
        if (!zipEntryName.startsWith(TESSDATA_SX + "/")) {
          zipInputStream.closeEntry();
          continue;
        }
        LOG.debug("Zip entry: {}", zipEntry);
        File cachedFile = new File(cachedTessdataDirectory, zipEntryName.substring(TESSDATA_SX_SL.length()));
        if (zipEntryName.endsWith("/")) {
          LOG.debug("Cached element {} is a directory", cachedFile);
          if (!cachedFile.exists()) {
            LOG.debug("Cached directory {} doesn't exist, creating it", cachedFile);
            if (!cachedFile.mkdirs()) {
              LOG.error("Could not create directory {}; aborting tessdata copy", cachedFile);
              return;
            }
          }
          zipInputStream.closeEntry();
          continue;
        }
        if (cachedFile.exists() && cachedFile.length() == zipEntry.getSize()) {
          LOG.debug("Cached file {} exists, checked size against the zip entry's is OK", cachedFile);
          zipInputStream.closeEntry();
          continue;
        }
        if (cachedFile.exists()) {
          LOG.debug("Cached file {} exists, deleting it before copy", cachedFile);
          if (!cachedFile.delete()) {
            LOG.error("Could not delete file {} before copy; aborting tessdata copy", cachedFile);
            return;
          }
        }
        LOG.debug("Copying file {}", cachedFile);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(cachedFile))) {
          zipInputStream.transferTo(outputStream);
        }
        zipInputStream.closeEntry();
      }
    }
    catch (IOException e) {
      LOG.error("Exception while unzipping {}. Exception is: {}", jarFile, e);
    }
  }

}
