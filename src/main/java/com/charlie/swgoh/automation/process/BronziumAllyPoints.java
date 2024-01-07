package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.BronziumScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BronziumAllyPoints extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(BronziumAllyPoints.class);

  private final int targetAllyPoints;

  public BronziumAllyPoints(String targetAllyPoints) {
    this.targetAllyPoints = BronziumScreen.parseAllyPoints(targetAllyPoints);
  }

  @Override
  protected void doProcess() {
    LOG.info("Collecting bronziums with target ally points of {}", targetAllyPoints);

    // Check initial state
    BronziumScreen.State state = BronziumScreen.readState();
    if (state != BronziumScreen.State.TITLE_BUY && state != BronziumScreen.State.TITLE_FREE && state != BronziumScreen.State.TITLE_WAITING) {
      throw new ProcessException("Starting screen is not the bronzium one");
    }
    int startAllyPoints = BronziumScreen.readTitleAllyPoints();
    LOG.info("Starting ally points: {}", startAllyPoints);
    if (startAllyPoints <= targetAllyPoints) {
      String msg = "Target already reached";
      LOG.info(msg);
      setMessage(msg);
      return;
    }

    int numberOfBronziumsToOpen = 1 + (startAllyPoints - targetAllyPoints) / 250;
    int numberOfBronziumsLeftToOpen = numberOfBronziumsToOpen;

    while (true) {
      handleKeys();

      AutomationUtil.mouseMove(BronziumScreen.L_IDLE, "Move mouse to idle position");
      state = BronziumScreen.readState();
      LOG.info("Read state: {}", state);

      switch (state) {
        case TITLE_BUY:
        case TITLE_FREE:
        case TITLE_WAITING:
          AutomationUtil.click(BronziumScreen.L_BRONZIUM_BUY_BUTTON, "Clicking on BUY button");
          numberOfBronziumsLeftToOpen--;
          feedback(numberOfBronziumsLeftToOpen, numberOfBronziumsToOpen);
          break;
        case OPEN_SKIP:
          AutomationUtil.click(BronziumScreen.L_SKIP_BUTTON, "Click SKIP button");
          break;
        case OPEN_CONTINUE:
          AutomationUtil.click(BronziumScreen.L_CONTINUE_BUTTON, "Click CONTINUE button");
          break;
        case OPEN_BUY_AGAIN_FINISH:
          if (numberOfBronziumsLeftToOpen == 0) {
            AutomationUtil.click(BronziumScreen.L_FINISH_BUTTON, "Click FINISH button");
            return;
          }
          else {
            AutomationUtil.click(BronziumScreen.L_BUY_AGAIN_BUTTON, "Click BUY AGAIN button");
            numberOfBronziumsLeftToOpen--;
            feedback(numberOfBronziumsLeftToOpen, numberOfBronziumsToOpen);
          }
          break;
        case UNKNOWN:
          break;
      }

      AutomationUtil.waitFor(500L);
    }
  }

  private void feedback(int numberLeft, int totalNumber) {
    setMessage("Number of bronziums left to open: " + numberLeft);
    progress = (double)(totalNumber - numberLeft) / (double)(totalNumber);
    updateProgressAndETA();
  }

}
