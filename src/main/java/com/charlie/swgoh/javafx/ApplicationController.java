package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.FeetbackStatus;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.automation.process.*;
import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    AbstractProcess process = new BronziumAllyPoints();
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

  public void loadProgressFile() {
    try {
      Progress progress = JsonConnector.readProgressFromFile(progressFileName.getText());
      allyCode.getItems().clear();
      allyCode.getItems().addAll(
              progress.getProfiles().stream().map(Profile::getAllyCode).collect(Collectors.toList())
      );
      if (allyCode.getItems().size() == 1) {
        allyCode.setValue(allyCode.getItems().get(0));
      }
      setMessage("File succesfully loaded. Number of profiles: " + progress.getProfiles().size());
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
    }
  }

  public void runReadUnequippedMods() {
    String ally = allyCode.getValue();
    String fileName = progressFileName.getText();

    AbstractProcess process = new ReadUnequippedMods();
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

  public void checkMoveModsFile() {
    String fileName = moveModsFileName.getText();
    try {
      Map<String, List<Mod>> modMap = HtmlConnector.getModsByCharacterFromHTML(fileName);
      setMessage("File succesfully loaded. Number of characters: " + modMap.size());
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
    }
  }

  public void runMoveMods() {
    String fileName = moveModsFileName.getText();

    AbstractProcess process = new MoveMods();
    process.setFeedback(this);
    process.setParameters(fileName);
    process.process();
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  @Override
  public void setStatus(FeetbackStatus feetbackStatus) {
    Platform.runLater(() -> status.setText(feetbackStatus.toString()));
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
