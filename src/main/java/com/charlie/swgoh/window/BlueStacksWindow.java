package com.charlie.swgoh.window;

import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

public abstract class BlueStacksWindow extends EmulatorWindow {

  private static final Logger LOG = LoggerFactory.getLogger(BlueStacksWindow.class);

  private static final int TARGET_WIDTH = 1280;
  private static final int TARGET_HEIGHT = 720;

  private Win32Data bluestacks;
  private Win32Data viewport;

  private Region window;

  protected abstract String getClassNameForBluestacks();
  protected abstract String getClassNameForViewport();
  protected abstract String getWindowTextForBluestacks();
  protected abstract String getWindowTextForViewport();

  @Override
  public void showAndAdjust() {
    LOG.info("Showing and adjusting BlueStacks window");
    identifyWindows();
    showWindow();
    resizeAndMove();
    AutomationUtil.waitForDelay();
    window = new Region(Win32Util.getWindowRectangle(viewport.getHwnd()));
  }

  @Override
  public Region getWindow() {
    return window;
  }

  private void identifyWindows() {
    LOG.info("Identifying BlueStacks window");
    bluestacks = new Win32Data();
    viewport = new Win32Data();
    Win32Util.enumerateWindows(win32Data -> {
      LOG.debug("Win32Data: {}", win32Data);
      if (
              win32Data.getClassName().toUpperCase(Locale.ROOT).startsWith(getClassNameForBluestacks()) &&
                      win32Data.getWindowText().startsWith(getWindowTextForBluestacks())
      ) {
        bluestacks.copyFrom(win32Data);
      }
      if (
              win32Data.getClassName().toUpperCase(Locale.ROOT).startsWith(getClassNameForViewport()) &&
                      win32Data.getWindowText().startsWith(getWindowTextForViewport())
      ) {
        viewport.copyFrom(win32Data);
      }
      return bluestacks.getHwnd() == null || viewport.getHwnd() == null;
    });

    if (bluestacks.getHwnd() == null || viewport.getHwnd() == null) {
      throw new ProcessException("Could not find the Bluestacks window. Aborting.");
    }
  }

  private void showWindow() {
    Win32Util.showWindow(bluestacks);
  }

  private void resizeAndMove() {
    int widthDiff = bluestacks.getRect().width - viewport.getRect().width;
    int heightDiff = bluestacks.getRect().height - viewport.getRect().height;

    Rectangle newRect = new Rectangle(bluestacks.getRect().x, bluestacks.getRect().y, TARGET_WIDTH + widthDiff, TARGET_HEIGHT + heightDiff);

    Win32Util.moveWindow(bluestacks, newRect);
  }

}
