package com.charlie.swgoh.automation;

import com.charlie.swgoh.exception.ProcessException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

public class BlueStacksApp {

  private BlueStacksApp() {}

  private static final Logger LOG = LoggerFactory.getLogger(BlueStacksApp.class);

  static {
    LOG.debug("Loading native user32.dll");
  }
  private static final User32 USER_32 = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

  private static final int TARGET_WIDTH = 1280;
  private static final int TARGET_HEIGHT = 720;

  private static class WindowHolder {
    private WinDef.HWND hwnd = null;
    private Rectangle rectangle = null;

    public void setHwnd(WinDef.HWND hwnd) {
      this.hwnd = hwnd;
    }

    public void setRectangle(Rectangle rectangle) {
      this.rectangle = rectangle;
    }

    public WinDef.HWND getHwnd() {
      return hwnd;
    }

    public Rectangle getRectangle() {
      return rectangle;
    }
  }

  private static WindowHolder bluestacks;
  private static WindowHolder viewport;

  private static Region window;

  public static void showAndAdjust() {
    LOG.info("Showing and adjusting BlueStacks window");
    identifyWindows();
    showWindow();
    resizeAndMove();
    window = new Region(getWindowRectangle(viewport.getHwnd()));
  }

  public static Region getWindow() {
    return window;
  }

  private static void identifyWindows() {
    LOG.info("Identifying BlueStacks window");
    bluestacks = new WindowHolder();
    viewport = new WindowHolder();
    USER_32.EnumWindows(
            (hwnd, pointer) -> {
              String className = getWindowClassName(hwnd);
              LOG.debug("ClassName: {}", className);
              if (className.toUpperCase(Locale.ROOT).startsWith("HWNDWRAPPER[BLUESTACKS.EXE")) {
                String windowText = getWindowText(hwnd);
                LOG.debug("WindowText: {}", windowText);
                if ("BlueStacks".equals(windowText)) {
                  bluestacks.setHwnd(hwnd);
                  bluestacks.setRectangle(getWindowRectangle(hwnd));
                }
                if ("KeymapCanvasWindow".equals(windowText)) {
                  viewport.setHwnd(hwnd);
                  viewport.setRectangle(getWindowRectangle(hwnd));
                }
              }
              return bluestacks.getHwnd() == null || viewport.getHwnd() == null;
            },
            Pointer.NULL
    );

    if (bluestacks.getHwnd() == null || viewport.getHwnd() == null) {
      throw new ProcessException("Could not find the Bluestacks window. Aborting.");
    }
  }

  private static void showWindow() {
    LOG.info("Switching BlueStacks to the foreground");
    USER_32.SetForegroundWindow(bluestacks.getHwnd());
  }

  private static void resizeAndMove() {
    LOG.info("Moving and resizing BlueStacks window");
    int widthDiff = bluestacks.getRectangle().width - viewport.getRectangle().width;
    int heightDiff = bluestacks.getRectangle().height - viewport.getRectangle().height;

    int newX = 0;
    int newY = 0;
    int newW = TARGET_WIDTH + widthDiff;
    int newH = TARGET_HEIGHT + heightDiff;

    USER_32.MoveWindow(bluestacks.getHwnd(), newX, newY, newW, newH, true);
  }

  private static String getWindowClassName(WinDef.HWND hwnd) {
    char[] classNameBuffer = new char[255];
    int classNameBufferLength = USER_32.GetClassName(hwnd, classNameBuffer, classNameBuffer.length);
    return String.valueOf(classNameBuffer, 0, classNameBufferLength);
  }

  private static String getWindowText(WinDef.HWND hwnd) {
    char[] windowTextBuffer = new char[255];
    int windowTextBufferLength = USER_32.GetWindowText(hwnd, windowTextBuffer, windowTextBuffer.length);
    return String.valueOf(windowTextBuffer, 0, windowTextBufferLength);
  }

  private static Rectangle getWindowRectangle(WinDef.HWND hwnd) {
    WinDef.RECT rect = new WinDef.RECT();
    USER_32.GetWindowRect(hwnd, rect);
    return rect.toRectangle();
  }

}
