package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
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
    this.targetAllyPoints = AutomationUtil.parseAllyPoints(parameters[0]);
    LOG.info("Target ally points: {}", targetAllyPoints);
  }

  @Override
  public void init() {
    BlueStacksApp.showAndAdjust();
    BronziumScreen.init();
  }

  @Override
  public void doProcess() {
    LOG.info("Collecting bronziums with target ally points of {}", targetAllyPoints);

    // Check initial state
    BronziumScreen.State state = BronziumScreen.readState();

    if (state != BronziumScreen.State.TITLE_BUY && state != BronziumScreen.State.TITLE_FREE && state != BronziumScreen.State.TITLE_WAITING) {
      throw new ProcessException("Starting screen is not the bronzium one");
    }

    while (true) {
      AutomationUtil.handleKeys(this);

      AutomationUtil.mouseMove(BronziumScreen.getLocIdle(), "Move mouse to idle position");
      state = BronziumScreen.readState();
      LOG.info("Read state: {}", state);
      if (state == BronziumScreen.State.TITLE_BUY || state == BronziumScreen.State.TITLE_FREE || state == BronziumScreen.State.TITLE_WAITING) {
        int allyPoints = BronziumScreen.readTitleAllyPoints();
        if (allyPoints < targetAllyPoints) {
          LOG.info("Target reached: {}", targetAllyPoints);
          return;
        }
        AutomationUtil.click(BronziumScreen.getLocBronziumBuyButton(), "Clicking on BUY button");
      }
      else if (state == BronziumScreen.State.OPEN_SKIP) {
        AutomationUtil.click(BronziumScreen.getLocSkipButton(), "Click SKIP button");
      }
      else if (state == BronziumScreen.State.OPEN_CONTINUE) {
        AutomationUtil.click(BronziumScreen.getLocContinueButton(), "Click CONTINUE button");
      }
      else if (state == BronziumScreen.State.OPEN_BUY_AGAIN_FINISH) {
        int allyPoints = BronziumScreen.readOpenAllyPoints();
        LOG.info("Current ally points: {}", allyPoints);
        if (allyPoints < targetAllyPoints) {
          LOG.info("Target reached: {}", targetAllyPoints);
          LOG.info("Finished");
          return;
        }
        AutomationUtil.click(BronziumScreen.getLocBuyAgainButton(), "Click BUY AGAIN button");
      }
      AutomationUtil.waitFor(500L);
    }
  }

}
