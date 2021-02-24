package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.exception.ProcessException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class AdjustWindow implements IProcess {

  private static final Logger LOG = LoggerFactory.getLogger(AdjustWindow.class);

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

  @Override
  public void process() {
    LOG.info("Showing and adjusting BlueStacks window");
    showWindow();
    resizeAndMove();
  }

  private void showWindow() {
    LOG.info("Switching BlueStacks to the foreground");
    BlueStacksApp.focus();
  }

  private void resizeAndMove() {
    WindowHolder bluestacks = new WindowHolder();
    WindowHolder viewport = new WindowHolder();

    USER_32.EnumWindows(
            (hwnd, pointer) -> {
              String className = getWindowClassName(hwnd);
              if (className.startsWith("HwndWrapper[Bluestacks.exe")) {
                String windowText = getWindowText(hwnd);
                if ("BlueStacks".equals(windowText)) {
                  bluestacks.setHwnd(hwnd);
                  bluestacks.setRectangle(getWindowRectangle(hwnd));
                }
                if ("KeymapCanvasWindow".equals(windowText)) {
                  viewport.setHwnd(hwnd);
                  viewport.setRectangle(getWindowRectangle(hwnd));
                }
              }
              return true;
            },
            Pointer.NULL
    );

    if (bluestacks.getHwnd() == null || viewport.getHwnd() == null) {
      throw new ProcessException("Could not find the Bluestacks window. Aborting.");
    }

    int widthDiff = bluestacks.getRectangle().width - viewport.getRectangle().width;
    int heightDiff = bluestacks.getRectangle().height - viewport.getRectangle().height;

    int newX = 0;
    int newY = 0;
    int newW = TARGET_WIDTH + widthDiff;
    int newH = TARGET_HEIGHT + heightDiff;

    USER_32.MoveWindow(bluestacks.getHwnd(), newX, newY, newW, newH, true);
  }

  private String getWindowClassName(WinDef.HWND hwnd) {
    char[] classNameBuffer = new char[255];
    int classNameBufferLength = USER_32.GetClassName(hwnd, classNameBuffer, classNameBuffer.length);
    return String.valueOf(classNameBuffer, 0, classNameBufferLength);
  }

  private String getWindowText(WinDef.HWND hwnd) {
    char[] windowTextBuffer = new char[255];
    int windowTextBufferLength = USER_32.GetWindowText(hwnd, windowTextBuffer, windowTextBuffer.length);
    return String.valueOf(windowTextBuffer, 0, windowTextBufferLength);
  }

  private Rectangle getWindowRectangle(WinDef.HWND hwnd) {
    WinDef.RECT rect = new WinDef.RECT();
    USER_32.GetWindowRect(hwnd, rect);
    return rect.toRectangle();
  }

}
