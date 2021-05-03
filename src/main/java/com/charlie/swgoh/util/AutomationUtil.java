package com.charlie.swgoh.util;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
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

  private static final double WAIT_FOR_IMAGE_DURATION = 5.0;

  public static Location getShiftedLocation(Location location) {
    return new Location(
            location.getX() + BlueStacksApp.getWindow().getX(),
            location.getY() + BlueStacksApp.getWindow().getY()
    );
  }

  public static Region getShiftedRegion(Region region) {
    return new Region(
            region.getX() + BlueStacksApp.getWindow().getX(),
            region.getY() + BlueStacksApp.getWindow().getY(),
            region.getW(),
            region.getH()
    );
  }

  public static void mouseMove(Location location, String description) {
    try {
      LOG.debug( "{}: moving to {}", description, location);
      BlueStacksApp.getWindow().mouseMove(getShiftedLocation(location));
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void mouseMoveOffset(int xoff, int yoff, String description) {
    LOG.debug("Move mouse xoff={}, yoff={}: {}", xoff, yoff, description);
    BlueStacksApp.getWindow().mouseMove(xoff, yoff);
  }

  public static void mouseDown(int buttons, String description) {
    LOG.debug("Mouse down (buttons={}): {}", buttons, description);
    BlueStacksApp.getWindow().mouseDown(buttons);
  }

  public static void mouseUp(String description) {
    LOG.debug("Mouse up: {}", description);
    BlueStacksApp.getWindow().mouseUp();
  }

  public static void dragDrop(Location fromLocation, Location toLocation, String description) throws FindFailed {
    LOG.debug("Drag drop from={}, to={}: {}", fromLocation, toLocation, description);
    BlueStacksApp.getWindow().dragDrop(getShiftedLocation(fromLocation), getShiftedLocation(toLocation));
  }

  public static void click(Location location, String description) {
    try {
      LOG.debug( "{}: clicking on {}", description, location);
      BlueStacksApp.getWindow().click(getShiftedLocation(location));
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
    BlueStacksApp.getWindow().type(text);
  }

  public static String readLine(Region region) {
    String text = getShiftedRegion(region).textLine();
    LOG.debug("Read line in {}: {}", region, text);
    return text;
  }

  public static List<String> readLines(Region region) {
    List<String> lines = getShiftedRegion(region).textLines();
    LOG.debug("Read lines in {}: {}", region, lines);
    return lines;
  }

  public static Location getLocation(int x, int y) {
    return new Location(x, y);
  }

  public static Region getRegion(int x, int y, int w, int h) {
    return new Region(x, y, w, h);
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
    return getShiftedRegion(region).has(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean checkForPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    return getShiftedRegion(region).has(pattern, 0.1 * Configuration.getSpeed().getDelayMultiplier());
  }

  public static List<Match> findAllPatterns(Region region, Pattern pattern, String description) {
    List<Match> result = getShiftedRegion(region).findAllList(pattern);
    LOG.debug("{}: {} matches", description, result.size());
    return result;
  }

  public static String takeScreenshot(String directory) {
    ScreenImage screenImage = Screen.getPrimaryScreen().capture(BlueStacksApp.getWindow());
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

}
