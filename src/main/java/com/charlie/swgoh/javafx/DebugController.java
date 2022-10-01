package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.datamodel.InputType;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.ModStat;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.window.EmulatorWindow;
import com.charlie.swgoh.window.Win32Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DebugController {

  @FXML
  private TextField xText;

  @FXML
  private TextField yText;

  @FXML
  private TextField wText;

  @FXML
  private TextField hText;

  private IFeedback feedback;
  private Region debugRegion = new Region(600, 500, 200, 100);

  private static final Logger LOG = LoggerFactory.getLogger(DebugController.class);

  public void init(Stage primaryStage, Stage debugStage, IFeedback feedback) {
    debugStage.setX(primaryStage.getX());
    debugStage.setY(primaryStage.getY() + primaryStage.getHeight() + 20.0);
    this.feedback = feedback;
  }

  public void adjustWindow() {
    Configuration.configure();
    EmulatorWindow.init();
    EmulatorWindow.INSTANCE.showAndAdjust();
  }

  public void takeScreenshot() {
    feedback.setMessage("Screenshot: " + AutomationUtil.takeScreenshot());
  }

  public void changeRegion(ActionEvent actionEvent) {
    String data = (String) ((Node) actionEvent.getSource()).getUserData();
    String[] parts = data.split("\\|");
    String corner = parts[0];
    String direction = parts[1];
    int value = Integer.parseInt(parts[2]);

    int x = debugRegion.x;
    int y = debugRegion.y;
    int w = debugRegion.w;
    int h = debugRegion.h;

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
    writeTextFieldsFromRegion();
  }

  public void readRegionFromTextFields() {
    try {
      debugRegion = new Region(
              Integer.parseInt(xText.getText()),
              Integer.parseInt(yText.getText()),
              Integer.parseInt(wText.getText()),
              Integer.parseInt(hText.getText())
      );
      highlightRegion();
    }
    catch (NumberFormatException e) {
      // Do nothing
    }
  }

  private void writeTextFieldsFromRegion() {
    xText.setText(String.valueOf(debugRegion.x));
    yText.setText(String.valueOf(debugRegion.y));
    wText.setText(String.valueOf(debugRegion.w));
    hText.setText(String.valueOf(debugRegion.h));
    highlightRegion();
  }

  private void highlightRegion() {
    AutomationUtil.highlight(debugRegion);
  }

  public void readSelection() {
    if (debugRegion == null) {
      return;
    }
    feedback.setMessage("Read selection: " + AutomationUtil.readLine(debugRegion));
    highlightRegion();
  }

  public void enumerateWindows() {
    Win32Util.enumerateWindows(win32Data -> {
      LOG.debug("Window: {}", win32Data);
      return true;
    });
  }

  public void saveRegionToPNG() {
    if (debugRegion == null) {
      return;
    }
    BufferedImage bufferedImage = AutomationUtil.getBufferedImageFromRegion(debugRegion);
    File file = new File(AutomationUtil.TEMP_DIRECTORY, System.currentTimeMillis() + ".png");
    try {
      ImageIO.write(bufferedImage, "png", file);
      feedback.setMessage(file.getAbsolutePath());
    }
    catch (IOException e) {
      LOG.error("Exception while writing image file", e);
    }
  }

  public void modScreenFilterSecondaryStats() {
    Mod mod = new Mod();
    mod.setSlot(ModSlot.DIAMOND);
    mod.setSet(ModSet.TENACITY);
    mod.setPrimaryStat(new ModStat("42% CRIT DAMAGE", InputType.GAME));
    mod.setSecondaryStats(Arrays.asList(
            new ModStat("(1) +150 Health", InputType.GAME),
            new ModStat("(2) +12 Speed", InputType.GAME),
            new ModStat("(3) +4.5% Tenacity", InputType.GAME),
            new ModStat("(4) +4 Defense", InputType.GAME)
    ));

    ModScreen.enterModFilter();
    if (!ModScreenFilter.waitForTitle()) {
      throw new ProcessException("Mod screen filter: title not found. Aborting.");
    }
    ModScreenFilter.clickDefaultAndEnsureAnySlotIsOnTop();
    try {
      ModScreenFilter.filterForMod(mod);
    }
    catch (ProcessException e) {
      ModScreenFilter.closeWithoutConfirm();
    }
    //ModScreenFilter.confirm();
  }

}
