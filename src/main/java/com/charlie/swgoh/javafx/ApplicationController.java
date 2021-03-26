package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.process.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApplicationController {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);

  @FXML
  private TabPane tabPane;

  @FXML
  private TextField targetAllyPoints;

  @FXML
  private ComboBox<String> allyCode;

  @FXML
  private TextField progressFileName;

  @FXML
  private TextField moveModsFileName;

  private Stage primaryStage;

  private final Map<String, Runnable> runnableMap = new HashMap<>();

  public ApplicationController() {
    runnableMap.put("bronziumDailyTab", this::runBronziumDaily);
    runnableMap.put("bronziumAllyPointsTab", this::runBronziumAllyPoints);
    runnableMap.put("readUnequippedModsTab", this::runReadUnequippedMods);
    runnableMap.put("moveModsTab", this::runMoveMods);
  }

  public void about() {
    LOG.info(getActiveTabId());
  }

  public void run() {
    String activeTabId = getActiveTabId();
    if (activeTabId == null) {
      return;
    }
    Runnable runnable = runnableMap.get(activeTabId);
    if (runnable != null) {
      runnable.run();
    }
  }

  private String getActiveTabId() {
    for (Tab tab : tabPane.getTabs()) {
      if (tab.isSelected()) {
        return tab.getId();
      }
    }
    return null;
  }

  public void runBronziumDaily() {
    try {
      IProcess process = new BronziumDaily();
      process.process();
    }
    catch (Exception e) {
      // Do nothing for now
    }
  }

  public void runBronziumAllyPoints() {
    try {
      String target = targetAllyPoints.getText();

      IProcess process = new BronziumAllyPoints();
      process.setParameters(target);
      process.process();
    } catch (Exception e) {
      // Do nothing for now
    }
  }

  public void chooseProgressFile() {
    FileChooser fileChooser = new FileChooser();
    File progressFile = fileChooser.showOpenDialog(primaryStage);
    if (progressFile != null) {
      progressFileName.setText(progressFile.getAbsolutePath());
    }
  }

  public void runReadUnequippedMods() {
    try {
      String ally = allyCode.getValue();
      String fileName = progressFileName.getText();

      IProcess process = new ReadUnequippedMods();
      process.setParameters(ally, fileName);
      process.process();
    } catch (Exception e) {
      // Do nothing for now
    }
  }

  public void chooseMoveModsFile() {
    FileChooser fileChooser = new FileChooser();
    File moveModsFile = fileChooser.showOpenDialog(primaryStage);
    if (moveModsFile != null) {
      moveModsFileName.setText(moveModsFile.getAbsolutePath());
    }
  }

  public void runMoveMods() {
    try {
      String fileName = moveModsFileName.getText();

      IProcess process = new MoveMods();
      process.setParameters(fileName);
      process.process();

    } catch (Exception e) {
      // Do nothing for now
    }
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

}
