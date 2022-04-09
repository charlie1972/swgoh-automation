package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class BronziumScreen {

  private BronziumScreen() {}

  public enum State {
    UNKNOWN,
    TITLE_WAITING,
    TITLE_FREE,
    TITLE_BUY,
    OPEN_SKIP,
    OPEN_CONTINUE,
    OPEN_BUY_AGAIN_FINISH
  }

  private static final Logger LOG = LoggerFactory.getLogger(BronziumScreen.class);

  // Regex
  private static final java.util.regex.Pattern REGEX_ALLY_POINTS = java.util.regex.Pattern.compile("([0-9.,]+)([KM]?)\\s*.*");

  // Image patterns
  static {
    Configuration.configureImagePath();
  }
  private static final Pattern P_TITLE = new Pattern("bronzium_title.png");
  private static final Pattern P_FREE_TEXT = new Pattern("bronzium_free_text.png");
  private static final Pattern P_FREE_BUTTON = new Pattern("bronzium_free_button.png");
  private static final Pattern P_BUY_AGAIN = new Pattern("bronzium_buy_again.png");
  private static final Pattern P_SKIP = new Pattern("bronzium_skip.png");
  private static final Pattern P_CONTINUE = new Pattern("bronzium_continue.png");
  private static final Pattern P_FINISH = new Pattern("bronzium_finish.png");

  // Locations
  public static final Location L_IDLE = new Location(995, 670);
  public static final Location L_BRONZIUM_BUY_BUTTON = new Location(1000, 650);
  public static final Location L_SKIP_BUTTON = new Location(1030, 670);
  public static final Location L_CONTINUE_BUTTON = L_SKIP_BUTTON;
  public static final Location L_BUY_AGAIN_BUTTON = new Location(960, 670);
  public static final Location L_FINISH_BUTTON = L_SKIP_BUTTON;

  // Regions
  public static final Region R_TITLE = new Region(972, 108, 266, 38);
  public static final Region R_FREE_TEXT = new Region(1010, 583, 94, 33);
  public static final Region R_FREE_BUTTON = new Region(1072, 637, 67, 35);
  public static final Region R_BUY_AGAIN = new Region(746, 653, 130, 35);
  public static final Region R_SKIP = new Region(1100, 652, 65, 36);
  public static final Region R_CONTINUE = new Region(1070, 653, 125, 35);
  public static final Region R_FINISH = new Region(1089, 653, 86, 35);
  public static final Region R_TITLE_ALLY_POINTS = new Region(827, 31, 82, 26);
  public static final Region R_OPEN_ALLY_POINTS = new Region(1149, 26, 129, 34);

  public static State readState() {
    if (AutomationUtil.checkForPattern(R_TITLE, P_TITLE, "Checking for title")) {
      if (AutomationUtil.checkForPattern(R_FREE_TEXT, P_FREE_TEXT, "Checking for Free text")) {
        return State.TITLE_WAITING;
      }
      if (AutomationUtil.checkForPattern(R_FREE_BUTTON, P_FREE_BUTTON, "Checking for FREE button")) {
        return State.TITLE_FREE;
      }
      return State.TITLE_BUY;
    }
    if (AutomationUtil.checkForPattern(R_SKIP, P_SKIP, "Checking for SKIP")) {
      return State.OPEN_SKIP;
    }
    if (AutomationUtil.checkForPattern(R_CONTINUE, P_CONTINUE, "Checking for CONTINUE")) {
      return State.OPEN_CONTINUE;
    }
    if (AutomationUtil.checkForPattern(R_FINISH, P_FINISH, "Checking for BUY AGAIN")) {
      return State.OPEN_BUY_AGAIN_FINISH;
    }
    return State.UNKNOWN;
  }

  public static int readTitleAllyPoints() {
    return returnParseAllyPointsAfterRetries(() -> parseAllyPoints(AutomationUtil.readLine(R_TITLE_ALLY_POINTS)));
  }

  public static int readOpenAllyPoints() {
    return returnParseAllyPointsAfterRetries(() -> parseAllyPoints(AutomationUtil.readLine(R_OPEN_ALLY_POINTS)));
  }

  private static int returnParseAllyPointsAfterRetries(Supplier<Integer> supplier) {
    for (int i = 0; i < 10; i++) {
      try {
        return supplier.get();
      }
      catch (Exception e) {
        AutomationUtil.waitFor(500L);
      }
    }
    return -1;
  }

  public static int parseAllyPoints(String str) {
    Matcher matcher = REGEX_ALLY_POINTS.matcher(str.toUpperCase(Locale.ROOT));
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

}
