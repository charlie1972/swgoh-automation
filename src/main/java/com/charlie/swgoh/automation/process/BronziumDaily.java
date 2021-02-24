package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.AppKeyHolder;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.BronziumScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BronziumDaily implements IProcess {

  private static final Logger LOG = LoggerFactory.getLogger(BronziumDaily.class);

  @Override
  public void process() {
    BronziumScreen.init();

    LOG.info("Collecting daily bronziums");

    // Check initial state
    BronziumScreen.State state = BronziumScreen.readState();

    if (state != BronziumScreen.State.TITLE_BUY && state != BronziumScreen.State.TITLE_FREE && state != BronziumScreen.State.TITLE_WAITING) {
      throw new ProcessException("Starting screen is not the bronzium one");
    }

    while (true) {
      AutomationUtil.handleKeys();

      AutomationUtil.mouseMove(BronziumScreen.getLocIdle(), "Move mouse to idle position");
      state = BronziumScreen.readState();
      LOG.info("Read state: {}", state);
      if (state == BronziumScreen.State.TITLE_BUY) {
        return;
      }
      else if (state == BronziumScreen.State.TITLE_FREE) {
        AutomationUtil.click(BronziumScreen.getLocBronziumBuyButton(), "Click FREE bronzium button");
      }
      else if (state == BronziumScreen.State.TITLE_WAITING) {
        AutomationUtil.waitFor(10000L);
      }
      else if (state == BronziumScreen.State.OPEN_SKIP) {
        AutomationUtil.click(BronziumScreen.getLocSkipButton(), "Click SKIP button");
      }
      else if (state == BronziumScreen.State.OPEN_CONTINUE) {
        AutomationUtil.click(BronziumScreen.getLocContinueButton(), "Click CONTINUE button");
      }
      else if (state == BronziumScreen.State.OPEN_BUY_AGAIN_FINISH) {
        AutomationUtil.click(BronziumScreen.getLocFinishButton(), "Click FINISH button");
      }
      AutomationUtil.waitFor(500L);
    }
  }

}
