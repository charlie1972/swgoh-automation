package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.AutomationSpeed;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.FeedbackStatus;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.automation.process.*;
import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.util.FileUtil;
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
import java.util.function.Consumer;
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
  private ChoiceBox<String> speed;

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

  public void init() {
    Configuration.setFeedback(this);
    Configuration.loadProperties();
    if (Configuration.getWindowX() == null || Configuration.getWindowY() == null) {
      Configuration.setWindowX(primaryStage.getX());
      Configuration.setWindowY(primaryStage.getY());
    }
    else {
      primaryStage.setX(Configuration.getWindowX());
      primaryStage.setY(Configuration.getWindowY());
    }

    primaryStage.xProperty().addListener((observable, oldValue, newValue) -> Configuration.setWindowX(primaryStage.getX()));
    primaryStage.yProperty().addListener((observable, oldValue, newValue) -> Configuration.setWindowY(primaryStage.getY()));

    for (AutomationSpeed s : AutomationSpeed.values()) {
      speed.getItems().add(s.getText());
    }
    speed.setValue(Configuration.getSpeed().getText());
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
    chooseFile(progressFileName::setText);
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
    chooseFile(moveModsFileName::setText);
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

  private void chooseFile(Consumer<String> consumer) {
    FileChooser fileChooser = new FileChooser();
    String defaultDirectory = Configuration.getDefaultDirectory();
    if (!defaultDirectory.isEmpty()) {
      fileChooser.setInitialDirectory(new File(defaultDirectory));
    }
    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
      String fullPath = file.getAbsolutePath();
      consumer.accept(fullPath);
      String directory = FileUtil.getFileComponents(fullPath).getDirectoryName();
      Configuration.setDefaultDirectory(directory);
    }
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public void onSpeedChange() {
    Configuration.setSpeed(AutomationSpeed.fromText(speed.getValue()));
  }

  @Override
  public void setStatus(FeedbackStatus feetbackStatus) {
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
