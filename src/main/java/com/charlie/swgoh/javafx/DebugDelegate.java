package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Region;

public class DebugDelegate {

  private final IFeedback feedback;

  private Region debugRegion;

  public DebugDelegate(IFeedback feedback) {
    this.feedback = feedback;
  }

  public void adjustWindow() {
    Configuration.configure();
    BlueStacksApp.showAndAdjust();
    feedback.setMessage("");
  }

  public void changeRegion(String data) {
    String[] parts = data.split("\\|");
    String corner = parts[0];
    String direction = parts[1];
    int value = Integer.parseInt(parts[2]);

    int x, y, w, h;
    if (debugRegion != null) {
      x = debugRegion.x;
      y = debugRegion.y;
      w = debugRegion.w;
      h = debugRegion.h;
    }
    else {
      x = 600;
      y = 500;
      w = 200;
      h = 100;
    }
    if ("TL".equals(corner)) {
      if ("H".equals(direction)) {
        x += value;
      }
      else {
        y += value;
      }
    }
    else {
      if ("H".equals(direction)) {
        w += value;
      }
      else {
        h += value;
      }
    }
    w = Math.max(1, w);
    h = Math.max(1, h);
    debugRegion = new Region(x, y, w, h);
    feedback.setMessage("Region: " + debugRegion);
    AutomationUtil.highlight(debugRegion);
  }

  public void readSelection() {
    if (debugRegion == null) {
      return;
    }
    String read = AutomationUtil.readLine(debugRegion);
    feedback.setMessage("Region: " + debugRegion + " / Read: " + read);
  }

}
