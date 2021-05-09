package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.BronziumScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BronziumDaily extends AbstractProcess {

  private static final Logger LOG = LoggerFactory.getLogger(BronziumDaily.class);

  @Override
  protected void doProcess() {
    LOG.info("Collecting daily bronziums");

    // Check initial state
    BronziumScreen.State state = BronziumScreen.readState();

    if (state != BronziumScreen.State.TITLE_BUY && state != BronziumScreen.State.TITLE_FREE && state != BronziumScreen.State.TITLE_WAITING) {
      throw new ProcessException("Starting screen is not the bronzium one");
    }

    long lastFreeBronziumTimeMillis = System.currentTimeMillis();
    int numberOfOpenedBronziums = 0;

    while (true) {
      handleKeys();

      double progress = (double)(System.currentTimeMillis() - lastFreeBronziumTimeMillis) / (10d * 60d * 1000d);
      setProgress(progress);
      setMessage("Number of bronziums opened during this run: " + numberOfOpenedBronziums);

      AutomationUtil.mouseMove(BronziumScreen.L_IDLE, "Move mouse to idle position");
      state = BronziumScreen.readState();
      LOG.info("Read state: {}", state);
      if (state == BronziumScreen.State.TITLE_BUY) {
        LOG.info("Finished");
        return;
      }
      else if (state == BronziumScreen.State.TITLE_FREE) {
        lastFreeBronziumTimeMillis = System.currentTimeMillis();
        numberOfOpenedBronziums++;
        AutomationUtil.click(BronziumScreen.L_BRONZIUM_BUY_BUTTON, "Click FREE bronzium button");
      }
      else if (state == BronziumScreen.State.TITLE_WAITING) {
        AutomationUtil.waitForFixed(10000L);
      }
      else if (state == BronziumScreen.State.OPEN_SKIP) {
        AutomationUtil.click(BronziumScreen.L_SKIP_BUTTON, "Click SKIP button");
      }
      else if (state == BronziumScreen.State.OPEN_CONTINUE) {
        AutomationUtil.click(BronziumScreen.L_CONTINUE_BUTTON, "Click CONTINUE button");
      }
      else if (state == BronziumScreen.State.OPEN_BUY_AGAIN_FINISH) {
        AutomationUtil.click(BronziumScreen.L_FINISH_BUTTON, "Click FINISH button");
      }
      AutomationUtil.waitFor(500L);
    }
  }

}
