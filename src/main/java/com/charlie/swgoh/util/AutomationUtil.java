package com.charlie.swgoh.util;

import com.charlie.swgoh.automation.AppKeyHolder;
import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.automation.process.IProcess;
import com.charlie.swgoh.datamodel.ModWithStatsInText;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class AutomationUtil {

  private AutomationUtil() {}

  private static final Logger LOG = LoggerFactory.getLogger(AutomationUtil.class);

  public static final long DELAY = 1000L;

  private static final java.util.regex.Pattern PATTERN_ALLY_POINTS = java.util.regex.Pattern.compile("([0-9.,]+)([KM]?)\\s*.*");
  private static final double WAIT_FOR_IMAGE_DURATION = 5.0;

  private static final int MOD_STAT_CERTAINTY_THRESHOLD = 90;
  private static final int MOD_STAT_PASSABLE_THRESHOLD = 50;

  public static <T> Match findRegion(Region region, T element, String description) {
    try {
      Match match = region.find(element);
      LOG.debug("{}: {}", description, match);
      return match;
    }
    catch (FindFailed ffe) {
      LOG.info("{}: not found. Exception is: {}", description, ffe);
      return null;
    }
  }

  public static <T> void mouseMoveRegion(Region region, T element, String description) {
    try {
      LOG.debug( "{}: moving to {}", description, element);
      region.mouseMove(element);
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static <T> void mouseMove(T element, String description) {
    mouseMoveRegion(BlueStacksApp.getWindow(), element, description);
  }

  public static <T> void clickRegion(Region region, T element, String description) {
    try {
      LOG.debug( "{}: clicking on {}", description, element);
      region.click(element);
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static <T> void click(T element, String description) {
    clickRegion(BlueStacksApp.getWindow(), element, description);
  }

  public static <T> void typeText(String text, String description) {
    LOG.debug( "{}: typing \"{}\"", description, text);
    BlueStacksApp.getWindow().type(text);
  }

  public static <T> void pasteTextRegion(Region region, T element, String text, String description) {
    try {
      LOG.debug( "{}: typing \"{}\" on {}", description, text, element);
      setClipboard(text);
      region.paste(element, text);
    }
    catch (FindFailed ffe) {
      throw new ProcessException(description);
    }
  }

  public static void pasteText(String text, String description) {
    LOG.debug( "{}: pasting \"{}\"", description, text);
    setClipboard(text);
    int result = BlueStacksApp.getWindow().paste(text);
    LOG.debug("Paste result: {}", result);
  }

  private static void setClipboard(String text) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    String textToSet = text != null ? text : "";
    StringSelection stringSelection = new StringSelection(textToSet);
    clipboard.setContents(stringSelection, null);
    try {
      long currentTime = System.currentTimeMillis();
      while (System.currentTimeMillis() - currentTime < 5000L) {
        if (textToSet.equals(clipboard.getData(DataFlavor.stringFlavor))) {
          return;
        }
      }
      waitFor(100L);
    }
    catch (Exception e) {
      throw new ProcessException("Exception " + e.getClass().getName() + ": " + e.getMessage());
    }
    throw new ProcessException("Could not set clipboard text. Aborting.");
  }

  public static String readLine(Region region) {
    String text = region.textLine();
    LOG.debug("Read line in {}: {}", region, text);
    return text;
  }

  public static List<String> readLines(Region region) {
    List<String> lines = region.textLines();
    LOG.debug("Read lines in {}: {}", region, lines);
    return lines;
  }

  public static Location getLocation(int x, int y) {
    return new Location(
            BlueStacksApp.getWindow().getX() + x,
            BlueStacksApp.getWindow().getY() + y
    );
  }

  public static Region getRegion(int x, int y, int w, int h) {
    return new Region(
            BlueStacksApp.getWindow().getX() + x,
            BlueStacksApp.getWindow().getY() + y,
            w,
            h
    );
  }

  public static void waitFor(Long millis) {
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
    return region.has(pattern, WAIT_FOR_IMAGE_DURATION);
  }

  public static boolean checkForPattern(Region region, Pattern pattern, String description) {
    LOG.debug(description);
    return region.has(pattern, 0.1);
  }

  public static int countPatterns(Region region, Pattern pattern, String description) {
    int count = region.findAllList(pattern).size();
    LOG.debug(description + ": {}", count);
    return count;
  }

  public static String takeScreenshot(String directory) {
    ScreenImage screenImage = Screen.getPrimaryScreen().capture(BlueStacksApp.getWindow());
    return screenImage.getFile(directory);
  }

  public static Location waitForMultiplePatternsAndGetLocation(Region region, List<Pattern> patterns, String description) {
    LOG.debug(description);
    List<Object> objList = new ArrayList<>(patterns);

    long startTimeMillis = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTimeMillis < 5000L) {
      List<Match> matches = region.findAnyList(objList);
      Optional<Location> optLocation = matches.stream()
              .filter(match -> match.getScore() > 0.7)
              .map(Match::getTarget)
              .findFirst();
      if (optLocation.isPresent()) {
        LOG.debug("{} -- found: {}", description, optLocation.get());
        return optLocation.get();
      }
      AutomationUtil.waitFor(100L);
    }
    LOG.error("{} -- not found", description);
    return null;
  }

  public static void handleKeys(IProcess process) {
    handleStop();
    boolean first = true;
    boolean hasPaused = false;
    while (AppKeyHolder.isPaused) {
      hasPaused = true;
      if (first) {
        LOG.info("*** Paused ***");
        first = false;
      }
      handleStop();
      waitForDelay();
    }
    if (hasPaused) {
      process.init();
    }
  }

  private static void handleStop() {
    if (AppKeyHolder.isStopping) {
      throw new ProcessException("Interrupted by user");
    }
  }

  public static int parseAllyPoints(String str) {
    Matcher matcher = PATTERN_ALLY_POINTS.matcher(str);
    if (!matcher.matches()) {
      throw new ProcessException("Unable to parse value: " + str);
    }
    double number = Double.parseDouble(matcher.group(1).replace(",", ""));
    if (matcher.group(2).isEmpty()) {
      return (int)number;
    }
    else {
      String multiplier = matcher.group(2);
      if ("K".equals(multiplier)) {
        return (int)(1000.0 * number);
      }
      if ("M".equals(multiplier)) {
        return (int)(1000000.0 * number);
      }
      throw new ProcessException("Unrecognized multiplier: " + multiplier);
    }
  }

  public static boolean matchMods(Mod referenceMod, ModWithStatsInText textMod) {
    LOG.debug("Fuzzy matching between this mod: {} and mod texts: {}", referenceMod, textMod);
    if (referenceMod.getSecondaryStats().size() != textMod.getSecondaryStats().size()) {
      LOG.debug("The secondary stats have different lengths. No match");
      return false;
    }

    List<Integer> scores = new ArrayList<>();
    scores.add(FuzzySearch.ratio(referenceMod.getPrimaryStat().toString(), stripSpaces(textMod.getPrimaryStat())));
    for (int i = 0; i < referenceMod.getSecondaryStats().size(); i++) {
      scores.add(FuzzySearch.ratio(referenceMod.getSecondaryStats().get(i).toString(), stripSpaces(textMod.getSecondaryStats().get(i))));
    }

    int nbPassable = (int) scores.stream().filter(score -> score >= MOD_STAT_PASSABLE_THRESHOLD).count();
    int nbCertain = (int) scores.stream().filter(score -> score >= MOD_STAT_CERTAINTY_THRESHOLD).count();
    // Criteria (both of them)
    // All passable
    // Certain: all except one, or all of them
    boolean result = (nbPassable == referenceMod.getSecondaryStats().size() + 1) && (nbCertain >= referenceMod.getSecondaryStats().size());
    LOG.debug("Scores: {}, passable: {}, certain: {}, result: {}", scores, nbPassable, nbCertain, result);
    return result;
  }

  public static String stripSpaces(String s) {
    return s.replace(" ", "");
  }

}
