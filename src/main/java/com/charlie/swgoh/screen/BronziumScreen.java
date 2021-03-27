package com.charlie.swgoh.screen;

import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

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

  private static final Pattern TITLE = new Pattern("bronzium_title.png");
  private static final Pattern FREE_TEXT = new Pattern("bronzium_free_text.png");
  private static final Pattern FREE_BUTTON = new Pattern("bronzium_free_button.png");
  private static final Pattern BUY_AGAIN = new Pattern("bronzium_buy_again.png");
  private static final Pattern SKIP = new Pattern("bronzium_skip.png");
  private static final Pattern CONTINUE = new Pattern("bronzium_continue.png");
  private static final Pattern FINISH = new Pattern("bronzium_finish.png");

  private static Location locIdle;
  private static Location locBronziumBuyButton;
  private static Location locSkipButton;
  private static Location locContinueButton;
  private static Location locBuyAgainButton;
  private static Location locFinishButton;
  private static Region regTitle;
  private static Region regFreeText;
  private static Region regFreeButton;
  private static Region regBuyAgain;
  private static Region regSkip;
  private static Region regContinue;
  private static Region regFinish;
  private static Region regTitleAllyPoints;
  private static Region regOpenAllyPoints;

  public static State readState() {
    if (AutomationUtil.checkForPattern(getRegTitle(), TITLE, "Checking for title")) {
      if (AutomationUtil.checkForPattern(getRegFreeText(), FREE_TEXT, "Checking for Free text")) {
        return State.TITLE_WAITING;
      }
      if (AutomationUtil.checkForPattern(getRegFreeButton(), FREE_BUTTON, "Checking for FREE button")) {
        return State.TITLE_FREE;
      }
      return State.TITLE_BUY;
    }
    if (AutomationUtil.checkForPattern(getRegSkip(), SKIP, "Checking for SKIP")) {
      return State.OPEN_SKIP;
    }
    if (AutomationUtil.checkForPattern(getRegContinue(), CONTINUE, "Checking for CONTINUE")) {
      return State.OPEN_CONTINUE;
    }
    if (AutomationUtil.checkForPattern(getRegSkip(), SKIP, "Checking for SKIP")) {
      return State.OPEN_SKIP;
    }
    if (AutomationUtil.checkForPattern(getRegBuyAgain(), BUY_AGAIN, "Checking for BUY AGAIN")) {
      return State.OPEN_BUY_AGAIN_FINISH;
    }
    return State.UNKNOWN;
  }

  public static int readTitleAllyPoints() {
    return returnParseAllyPointsAfterRetries(() -> AutomationUtil.parseAllyPoints(AutomationUtil.readLine(getRegTitleAllyPoints())));
  }

  public static int readOpenAllyPoints() {
    return returnParseAllyPointsAfterRetries(() -> AutomationUtil.parseAllyPoints(AutomationUtil.readLine(getRegOpenAllyPoints())));
  }

  private static int returnParseAllyPointsAfterRetries(Supplier<Integer> supplier) {
    for (int i = 0; i < 2; i++) {
      try {
        return supplier.get();
      }
      catch (Exception e) {
        AutomationUtil.waitFor(1000L);
      }
    }
    return supplier.get();
  }

  public static void init() {
    locIdle = AutomationUtil.getLocation(995, 670);
    locBronziumBuyButton = AutomationUtil.getLocation(1000, 650);
    locSkipButton = AutomationUtil.getLocation(1030, 670);
    locContinueButton = locSkipButton;
    locBuyAgainButton = AutomationUtil.getLocation(960, 670);
    locFinishButton = locSkipButton;

    regTitle = AutomationUtil.getRegion(972, 108, 266, 38);
    regFreeText = AutomationUtil.getRegion(1010, 583, 94, 33);
    regFreeButton = AutomationUtil.getRegion(1072, 637, 67, 35);
    regBuyAgain = AutomationUtil.getRegion(746, 653, 130, 35);
    regSkip = AutomationUtil.getRegion(1100, 652, 65, 36);
    regContinue = AutomationUtil.getRegion(1070, 653, 125, 35);
    regFinish = AutomationUtil.getRegion(1089, 653, 86, 35);
    regTitleAllyPoints = AutomationUtil.getRegion(827, 31, 82, 26);
    regOpenAllyPoints = AutomationUtil.getRegion(1149, 26, 129, 34);
  }

  public static Location getLocIdle() {
    return locIdle;
  }

  public static Location getLocBronziumBuyButton() {
    return locBronziumBuyButton;
  }

  public static Location getLocSkipButton() {
    return locSkipButton;
  }

  public static Location getLocContinueButton() {
    return locContinueButton;
  }

  public static Location getLocBuyAgainButton() {
    return locBuyAgainButton;
  }

  public static Location getLocFinishButton() {
    return locFinishButton;
  }

  public static Region getRegTitle() {
    return regTitle;
  }

  public static Region getRegFreeText() {
    return regFreeText;
  }

  public static Region getRegFreeButton() {
    return regFreeButton;
  }

  public static Region getRegBuyAgain() {
    return regBuyAgain;
  }

  public static Region getRegSkip() {
    return regSkip;
  }

  public static Region getRegContinue() {
    return regContinue;
  }

  public static Region getRegFinish() {
    return regFinish;
  }

  public static Region getRegTitleAllyPoints() {
    return regTitleAllyPoints;
  }

  public static Region getRegOpenAllyPoints() {
    return regOpenAllyPoints;
  }
}
