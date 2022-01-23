package com.charlie.swgoh.window;

import com.sun.jna.platform.win32.WinDef;

import java.awt.*;

public class Win32Data {

  private WinDef.HWND hwnd = null;
  private String className;
  private String windowText;
  private Rectangle rect;

  public Win32Data() {}

  public Win32Data(WinDef.HWND hwnd, String className, String windowText, Rectangle rect) {
    this.hwnd = hwnd;
    this.className = className;
    this.windowText = windowText;
    this.rect = rect;
  }

  public void copyFrom(Win32Data data) {
    this.hwnd = data.getHwnd();
    this.className = data.getClassName();
    this.windowText = data.getWindowText();
    this.rect = data.getRect();
  }

  public WinDef.HWND getHwnd() {
    return hwnd;
  }

  public void setHwnd(WinDef.HWND hwnd) {
    this.hwnd = hwnd;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getWindowText() {
    return windowText;
  }

  public void setWindowText(String windowText) {
    this.windowText = windowText;
  }

  public Rectangle getRect() {
    return rect;
  }

  public void setRect(Rectangle rect) {
    this.rect = rect;
  }

  @Override
  public String toString() {
    return "Win32Data{" +
            "hwnd=" + hwnd +
            ", className='" + className + '\'' +
            ", windowText='" + windowText + '\'' +
            ", rect=" + rect +
            '}';
  }

}
