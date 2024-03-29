package com.charlie.swgoh.util;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.window.EmulatorWindow;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.sikuli.script.support.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AutomationUtil {

  private AutomationUtil() {}

  private static final Logger LOG = LoggerFactory.getLogger(AutomationUtil.class);

  private static final long DEBUG_DELAY = 100L;
  private static final double WAIT_FOR_IMAGE_DURATION = 10.0;

  public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
  public static final long DELAY = 1000L;

  public static Location getShiftedLocation(Location location) {
    return new Location(
            location.getX() + EmulatorWindow.INSTANCE.getWindow().getX(),
            location.getY() + EmulatorWindow.INSTANCE.getWindow().getY()
    );
  }

  public static Location getReverseShiftedLocation(Location location) {
    return new Location(
            location.getX() - EmulatorWindow.INSTANCE.getWindow().getX(),
            location.getY() - EmulatorWindow.INSTANCE.getWindow().getY()
    );
  }

  public static Region getShiftedRegion(Region region) {
    return new Region(
            region.getX() + EmulatorWindow.INSTANCE.getWindow().getX(),
            region.getY() + EmulatorWindow.INSTANCE.getWindow().getY(),
            region.getW(),
            region.getH()
    );
  }

  public static void mouseMove(Location location, String description) {
    try {
      LOG.debug( "{}: moving to {}", description, location);
      if (Configuration.isHighlight()) {
        highlightTemporarily(location);
      }
      EmulatorWindow.INSTANCE.getWindow().mouseMove(getShiftedLocation(location));
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void mouseMoveOffset(int xoff, int yoff, String description) {
    LOG.debug("Move mouse xoff={}, yoff={}: {}", xoff, yoff, description);
    EmulatorWindow.INSTANCE.getWindow().mouseMove(xoff, yoff);
  }

  public static void mouseDown(int buttons, String description) {
    LOG.debug("Mouse down (buttons={}): {}", buttons, description);
    EmulatorWindow.INSTANCE.getWindow().mouseDown(buttons);
  }

  public static void mouseUp(String description) {
    LOG.debug("Mouse up: {}", description);
    EmulatorWindow.INSTANCE.getWindow().mouseUp();
  }

  public static void dragDrop(Location fromLocation, Location toLocation, String description) {
    try {
      LOG.debug("Drag drop from={}, to={}: {}", fromLocation, toLocation, description);
      float moveMouseDelay = Settings.MoveMouseDelay;
      Settings.MoveMouseDelay = 0.5f;
      Settings.DelayBeforeMouseDown = 0f;
      Settings.DelayBeforeDrag = 0f;
      Settings.DelayAfterDrag = 0.2f;
      EmulatorWindow.INSTANCE.getWindow().dragDrop(getShiftedLocation(fromLocation), getShiftedLocation(toLocation));
      Settings.MoveMouseDelay = moveMouseDelay;
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void click(Location location, String description) {
    try {
      LOG.debug( "{}: clicking on {}", description, location);
      if (Configuration.isHighlight()) {
        highlightTemporarily(location);
      }
      EmulatorWindow.INSTANCE.getWindow().click(getShiftedLocation(location));
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void click(Region region, String description) {
    click(region.getCenter(), description);
  }

  public static <T> void typeText(String text, String description) {
    LOG.debug( "{}: typing \"{}\"", description, text);
    EmulatorWindow.INSTANCE.getWindow().type(text);
  }

  public static String readLine(Region region) {
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    String text = readLineFromBufferedImage(getShiftedRegion(region).getImage().get());
    LOG.debug("Read line in {}: {}", region, text);
    return text;
  }

  public static BufferedImage getBufferedImageFromRegion(Region region) {
    return getShiftedRegion(region).getImage().get();
  }

  public static String readLineWithPreprocessing(Region region, int threshold) {
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    BufferedImage bufferedImage = getBufferedImageFromRegion(region);
    preprocessBufferedImage(bufferedImage, threshold);
    String text = readLineFromBufferedImage(bufferedImage);
    LOG.debug("Read line in {}: {}", region, text);
    return text;
  }

  public static String readLineFromBufferedImage(BufferedImage bufferedImage) {
    String text;
    try {
      text = OCR.readLine(bufferedImage);
    }
    catch (SikuliXception e) {
      LOG.warn("OCR.textLine failed. Trying to do custom resource copy");
      initTessdata();
      try {
        text = OCR.readLine(bufferedImage);
      }
      catch (SikuliXception ee) {
        throw new ProcessException("OCR.textLine failed, you need to restart the application");
      }
    }
    return text;
  }

  // Clean the buffered image:
  // - convert to greyscale
  // - transform the white and light grey pixels into black, and white the rest
  private static void preprocessBufferedImage(BufferedImage bufferedImage, int threshold) {
    for (int x = 0; x < bufferedImage.getWidth(); x++) {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        if (getPixelLuminosity(bufferedImage, x, y) > threshold) {
          bufferedImage.setRGB(x, y, 0xFF000000); // black
        }
        else {
          bufferedImage.setRGB(x, y, 0xFFFFFFFF); // white
        }
      }
    }
/*
    if (Configuration.isHighlight()) {
      try {
        File imageFile = new File(TEMP_DIRECTORY, System.currentTimeMillis() + ".png");
        LOG.debug("Writing processed image to: {}", imageFile);
        ImageIO.write(bufferedImage, "png", imageFile);
      }
      catch (IOException e) {
        LOG.error("Exception while writing image file", e);
      }
    }
*/
  }

  public static int getPixelLuminosity(BufferedImage bufferedImage, int x, int y) {
    int rgb = bufferedImage.getRGB(x, y);
    int red = (rgb >> 16) & 0xFF;
    int green = (rgb >> 8) & 0xFF;
    int blue = rgb & 0xFF;
    return (int)(0.3f * (float)red + 0.59f * (float)green + 0.11f * (float)blue);
  }

  public static List<String> readLines(Region region) {
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    List<String> lines;
    try {
      lines = getShiftedRegion(region).textLines();
    }
    catch (SikuliXception e) {
      LOG.warn("OCR.textLine failed. Trying to do custom resource copy");
      initTessdata();
      try {
        lines = getShiftedRegion(region).textLines();
      }
      catch (SikuliXception ee) {
        throw new ProcessException("OCR.textLines failed, you need to restart the application");
      }
    }
    LOG.debug("Read lines in {}: {}", region, lines);
    return lines;
  }

  public static void waitFor(Long millis) {
    try {
      Thread.sleep((long)((double)millis * Configuration.getSpeed().getDelayMultiplier()));
    }
    catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  public static void waitForFixed(Long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  public static void waitForDelay() {
    waitFor(DELAY);
  }

  public static boolean waitForPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).has(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean waitForPatternVanish(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).waitVanish(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean checkForPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).has(pattern, 0.1 * Configuration.getSpeed().getDelayMultiplier());
  }

  public static Match findPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    try {
      Match match = getShiftedRegion(region).find(pattern);
      LOG.debug("{}: match at {}", description, match);
      return match;
    } catch (FindFailed e) {
      LOG.debug("{}: no match", description);
      return null;
    }
  }

  public static List<Match> findAllPatterns(Region region, Pattern pattern, String description) {
    if (Configuration.isHighlight()) {
      highlightTemporarily(region);
    }
    List<Match> result = getShiftedRegion(region).findAllList(pattern);
    LOG.debug("{}: {} matches", description, result.size());
    return result;
  }

  public static String takeScreenshot() {
    ScreenImage screenImage = Screen.getPrimaryScreen().capture(EmulatorWindow.INSTANCE.getWindow());
    return screenImage.getFile(TEMP_DIRECTORY);
  }

  public static Location waitForMultiplePatternsAndGetLocation(Region region, List<Pattern> patterns, String description) {
    LOG.debug(description);
    List<Object> objList = new ArrayList<>(patterns);

    Region shiftedRegion = getShiftedRegion(region);
    long startTimeMillis = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTimeMillis < 5000L) {
      List<Match> matches = shiftedRegion.findAnyList(objList);
      Optional<Location> optLocation = matches.stream()
              .filter(match -> match.getScore() > 0.7)
              .map(Match::getTarget)
              .findFirst();
      if (optLocation.isPresent()) {
        LOG.debug("{} -- found: {}", description, optLocation.get());
        return optLocation.get();
      }
      waitFor(100L);
    }
    LOG.error("{} -- not found", description);
    return null;
  }

  public static void highlightTemporarily(Region region) {
    Region shiftedRegion = getShiftedRegion(region);
    Region.highlightAllOff();
    shiftedRegion.highlight();
    waitForFixed(DEBUG_DELAY);
    Region.highlightAllOff();
  }

  public static void highlight(Region region) {
    Region shiftedRegion = getShiftedRegion(region);
    Region.highlightAllOff();
    shiftedRegion.highlight();
  }

  public static void highlightTemporarily(Location location) {
    Location shiftedLocation = getShiftedLocation(location);
    Region region = new Region(
            shiftedLocation.getX() - 1,
            shiftedLocation.getY() - 1,
            3,
            3
    );
    Region.highlightAllOff();
    region.highlight();
    waitForFixed(DEBUG_DELAY);
    Region.highlightAllOff();
  }

  // This method aims to fix the inability to copy the tessdata directory at OCR initialization
  private static void initTessdata() {
    // Initialize the RunTime singleton so that root directories are set, and get the tessdata directory
    File cachedTessdataDirectory = new File(RunTime.get().fSikulixAppPath, "SikulixTesseract/tessdata");
    // Check the existence of the sikuliX JAR containing the tessdata directory
    URL url = RunTime.class.getResource("/tessdataSX");
    if (url == null || !"jar".equals(url.getProtocol())) {
      throw new ProcessException("tessdata directory not found in sikuliX JAR, OCR will malfunction");
    }
    LOG.debug("Resource /tessdataSX found, URL is {}", url);
    // Extract name of JAR file
    String urlFile = url.getFile();
    int p = urlFile.lastIndexOf("!");
    if (!urlFile.startsWith("file:///") || p < 0) {
      throw new ProcessException("Resource /tessdataSX is not a file in a JAR, it should start with file:/// and have ! as separator: " + urlFile);
    }
    String jarFile = urlFile.substring(8, p);
    // Open JAR file as zip
    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jarFile))) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String zipEntryName = zipEntry.getName();
        // Copy only directories/files in directory tessdataSX
        if (!zipEntryName.startsWith("tessdataSX/")) {
          zipInputStream.closeEntry();
          continue;
        }
        LOG.debug("Zip entry: {}", zipEntry);
        File cachedFile = new File(cachedTessdataDirectory, zipEntryName.substring(11));
        if (zipEntryName.endsWith("/")) {
          LOG.debug("Cached element {} is a directory", cachedFile);
          if (!cachedFile.exists()) {
            LOG.debug("Cached directory {} doesn't exist, creating it", cachedFile);
            if (!cachedFile.mkdirs()) {
              throw new ProcessException("Could not create directory " + cachedFile + "; aborting tessdata copy");
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
            throw new ProcessException("Could not delete file " + cachedFile + " before copy; aborting tessdata copy");
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
      throw new ProcessException("Exception while unzipping " + jarFile + ". Exception is: " + e);
    }
  }

}
