package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.BronziumScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BronziumAllyPoints extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(BronziumAllyPoints.class);

  private int targetAllyPoints;

  @Override
  public void setParameters(String... parameters) {
    this.targetAllyPoints = BronziumScreen.parseAllyPoints(parameters[0]);
    LOG.info("Target ally points: {}", targetAllyPoints);
  }

  @Override
  protected void doProcess() {
    LOG.info("Collecting bronziums with target ally points of {}", targetAllyPoints);

    // Check initial state
    BronziumScreen.State state = BronziumScreen.readState();

    if (state != BronziumScreen.State.TITLE_BUY && state != BronziumScreen.State.TITLE_FREE && state != BronziumScreen.State.TITLE_WAITING) {
      throw new ProcessException("Starting screen is not the bronzium one");
    }

    int startAllyPoints = -1;
    while (true) {
      handleKeys();

      AutomationUtil.mouseMove(BronziumScreen.L_IDLE, "Move mouse to idle position");
      state = BronziumScreen.readState();
      LOG.info("Read state: {}", state);
      if (state == BronziumScreen.State.TITLE_BUY || state == BronziumScreen.State.TITLE_FREE || state == BronziumScreen.State.TITLE_WAITING) {
        int allyPoints = BronziumScreen.readTitleAllyPoints();
        if (startAllyPoints < 0) {
          startAllyPoints = allyPoints;
        }
        if (feedbackAndCheckAllyPoints(allyPoints, startAllyPoints)) {
          return;
        }
        AutomationUtil.click(BronziumScreen.L_BRONZIUM_BUY_BUTTON, "Clicking on BUY button");
      }
      else if (state == BronziumScreen.State.OPEN_SKIP) {
        AutomationUtil.click(BronziumScreen.L_SKIP_BUTTON, "Click SKIP button");
      }
      else if (state == BronziumScreen.State.OPEN_CONTINUE) {
        AutomationUtil.click(BronziumScreen.L_CONTINUE_BUTTON, "Click CONTINUE button");
      }
      else if (state == BronziumScreen.State.OPEN_BUY_AGAIN_FINISH) {
        int allyPoints = BronziumScreen.readOpenAllyPoints();
        if (startAllyPoints < 0) {
          startAllyPoints = allyPoints;
        }
        if (feedbackAndCheckAllyPoints(allyPoints, startAllyPoints)) {
          AutomationUtil.click(BronziumScreen.L_FINISH_BUTTON, "Click FINISH button");
          return;
        }
        AutomationUtil.click(BronziumScreen.L_BUY_AGAIN_BUTTON, "Click BUY AGAIN button");
      }
      AutomationUtil.waitFor(500L);
    }
  }

  // True: finished
  // False: not finished
  private boolean feedbackAndCheckAllyPoints(int allyPoints, int startAllyPoints) {
    setMessage("Current Ally Points: " + allyPoints);
    double progress = (double)(startAllyPoints - allyPoints) / (double)(startAllyPoints - targetAllyPoints);
    setProgress(progress);
    if (allyPoints < targetAllyPoints) {
      LOG.info("Target reached: {}", targetAllyPoints);
      return true;
    }
    return false;
  }

}
