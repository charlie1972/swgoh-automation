package com.charlie.swgoh.util;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.window.EmulatorWindow;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutomationUtil {

  private AutomationUtil() {}

  private static final Logger LOG = LoggerFactory.getLogger(AutomationUtil.class);

  public static final long DELAY = 1000L;

  private static final long DEBUG_DELAY = 100L;
  private static final double WAIT_FOR_IMAGE_DURATION = 5.0;

  public static Location getShiftedLocation(Location location) {
    return new Location(
            location.getX() + EmulatorWindow.INSTANCE.getWindow().getX(),
            location.getY() + EmulatorWindow.INSTANCE.getWindow().getY()
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
      if (Configuration.isDebug()) {
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
      EmulatorWindow.INSTANCE.getWindow().dragDrop(getShiftedLocation(fromLocation), getShiftedLocation(toLocation));
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void click(Location location, String description) {
    try {
      LOG.debug( "{}: clicking on {}", description, location);
      if (Configuration.isDebug()) {
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
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    String text = getShiftedRegion(region).textLine();
    LOG.debug("Read line in {}: {}", region, text);
    return text;
  }

  public static List<String> readLines(Region region) {
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    List<String> lines = getShiftedRegion(region).textLines();
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
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).has(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean waitForPatternVanish(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).waitVanish(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean checkForPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    return getShiftedRegion(region).has(pattern, 0.1 * Configuration.getSpeed().getDelayMultiplier());
  }

  public static List<Match> findAllPatterns(Region region, Pattern pattern, String description) {
    if (Configuration.isDebug()) {
      highlightTemporarily(region);
    }
    List<Match> result = getShiftedRegion(region).findAllList(pattern);
    LOG.debug("{}: {} matches", description, result.size());
    return result;
  }

  public static String takeScreenshot(String directory) {
    ScreenImage screenImage = Screen.getPrimaryScreen().capture(EmulatorWindow.INSTANCE.getWindow());
    return screenImage.getFile(directory);
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

}
