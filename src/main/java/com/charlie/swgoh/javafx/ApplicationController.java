package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.automation.process.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationController implements IFeedback {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);

  @FXML
  private TabPane tabPane;

  @FXML
  private Button aboutButton;

  @FXML
  private Button runButton;

  @FXML
  private TextField targetAllyPoints;

  @FXML
  private ComboBox<String> allyCode;

  @FXML
  private TextField progressFileName;

  @FXML
  private TextField moveModsFileName;

  @FXML
  private Label status;

  @FXML
  private Label message;

  @FXML
  private ProgressBar progress;

  private Stage primaryStage;

  @FunctionalInterface
  private interface RunnableWithException {
    void run() throws Exception;
  }

  private final Map<String, Runnable> runnableMap = new HashMap<>();

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
      executorService.execute(runnable);
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
    AbstractProcess process = new BronziumDaily();
    process.setFeedback(this);
    process.process();
  }

  public void runBronziumAllyPoints() {
    String target = targetAllyPoints.getText();

    AbstractProcess  process = new BronziumAllyPoints();
    process.setFeedback(this);
    process.setParameters(target);
    process.process();
  }

  public void chooseProgressFile() {
    FileChooser fileChooser = new FileChooser();
    File progressFile = fileChooser.showOpenDialog(primaryStage);
    if (progressFile != null) {
      progressFileName.setText(progressFile.getAbsolutePath());
    }
  }

  public void runReadUnequippedMods() {
    String ally = allyCode.getValue();
    String fileName = progressFileName.getText();

    AbstractProcess  process = new ReadUnequippedMods();
    process.setFeedback(this);
    process.setParameters(ally, fileName);
    process.process();
  }

  public void chooseMoveModsFile() {
    FileChooser fileChooser = new FileChooser();
    File moveModsFile = fileChooser.showOpenDialog(primaryStage);
    if (moveModsFile != null) {
      moveModsFileName.setText(moveModsFile.getAbsolutePath());
    }
  }

  public void runMoveMods() {
    String fileName = moveModsFileName.getText();

    AbstractProcess  process = new MoveMods();
    process.setFeedback(this);
    process.setParameters(fileName);
    process.process();
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  @Override
  public void setStatus(String statusString) {
    Platform.runLater(() -> status.setText(statusString));
  }

  @Override
  public void setMessage(String messageString) {
    Platform.runLater(() -> {
      message.setText(messageString);
      message.setTextFill(Paint.valueOf("BLACK"));
    });
  }

  @Override
  public void setErrorMessage(String errorMessageString) {
    Platform.runLater(() -> {
      message.setText(errorMessageString);
      message.setTextFill(Paint.valueOf("RED"));
    });
  }

  @Override
  public void setProgress(double progressDouble) {
    double tempProgress = Math.min(progressDouble, 1d);
    double boundProgress = Math.max(tempProgress, 0d);
    Platform.runLater(() -> progress.setProgress(boundProgress));
  }

  @Override
  public void setAllControlsDisabled(boolean disabled) {
    Platform.runLater(() -> {
      tabPane.setDisable(disabled);
      aboutButton.setDisable(disabled);
      runButton.setDisable(disabled);
    });
  }

}
