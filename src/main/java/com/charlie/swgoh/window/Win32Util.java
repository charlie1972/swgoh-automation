package com.charlie.swgoh.window;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.function.Function;

public class Win32Util {

  private Win32Util() {}

  private static final Logger LOG = LoggerFactory.getLogger(Win32Util.class);

  static {
    LOG.debug("Loading native user32.dll");
  }
  private static final User32 USER_32 = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

  public static void enumerateWindows(Function<Win32Data, Boolean> callback) {
    LOG.info("Enumerating windows");
    USER_32.EnumWindows(
            (hwnd, pointer) -> {
              String className = getWindowClassName(hwnd);
              String windowText = getWindowText(hwnd);
              Rectangle rect = getWindowRectangle(hwnd);
              return callback.apply(new Win32Data(hwnd, className, windowText, rect));
            },
            Pointer.NULL
    );
  }

  public static void showWindow(Win32Data data) {
    LOG.info("Switching window with HWND={} to the foreground", data.getHwnd());
    USER_32.SetForegroundWindow(data.getHwnd());
  }

  public static void moveWindow(Win32Data data, Rectangle target) {
    LOG.info("Moving and resizing window with HWND={} to {}", data.getHwnd(), target);
    USER_32.MoveWindow(data.getHwnd(), target.x, (int)target.getY(), (int)target.getWidth(), (int)target.getHeight(), true);
  }

  public static String getWindowClassName(WinDef.HWND hwnd) {
    char[] classNameBuffer = new char[255];
    int classNameBufferLength = USER_32.GetClassName(hwnd, classNameBuffer, classNameBuffer.length);
    return String.valueOf(classNameBuffer, 0, classNameBufferLength);
  }

  public static String getWindowText(WinDef.HWND hwnd) {
    char[] windowTextBuffer = new char[255];
    int windowTextBufferLength = USER_32.GetWindowText(hwnd, windowTextBuffer, windowTextBuffer.length);
    return String.valueOf(windowTextBuffer, 0, windowTextBufferLength);
  }

  public static Rectangle getWindowRectangle(WinDef.HWND hwnd) {
    WinDef.RECT rect = new WinDef.RECT();
    USER_32.GetWindowRect(hwnd, rect);
    return rect.toRectangle();
  }

}
