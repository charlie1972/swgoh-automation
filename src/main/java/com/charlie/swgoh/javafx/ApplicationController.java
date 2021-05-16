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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
  private HBox controls;

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
  private CheckBox dryRun;

  @FXML
  public TextField revertMoveModsFileName;

  @FXML
  public TextField revertProgressFileName;

  @FXML
  public ComboBox<String> revertAllyCode;

  @FXML
  public CheckBox revertDryRun;

  @FXML
  private Label status;

  @FXML
  private Label message;

  @FXML
  private ProgressBar progress;

  private Stage primaryStage;

  private static final String TAB_BRONZIUM_DAILY = "bronziumDailyTab";
  private static final String TAB_BRONZIUM_ALLY_POINTS = "bronziumAllyPointsTab";
  private static final String TAB_READ_UNEQUIPPED_MODS = "readUnequippedModsTab";
  private static final String TAB_MOVE_MODS = "moveModsTab";
  private static final String TAB_REVERT_MOVE_MODS = "revertMoveModsTab";

  private final Map<String, Runnable> featureMap = new HashMap<>();
  private final Map<String, String> aboutBoxTitleMap = new HashMap<>();
  private final Map<String, String> aboutBoxLayoutMap = new HashMap<>();

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  public ApplicationController() {
    featureMap.put(TAB_BRONZIUM_DAILY, this::runBronziumDaily);
    featureMap.put(TAB_BRONZIUM_ALLY_POINTS, this::runBronziumAllyPoints);
    featureMap.put(TAB_READ_UNEQUIPPED_MODS, this::runReadUnequippedMods);
    featureMap.put(TAB_MOVE_MODS, this::runMoveMods);
    featureMap.put(TAB_REVERT_MOVE_MODS, this::runRevertMoveMods);

    aboutBoxTitleMap.put(TAB_BRONZIUM_DAILY, "About Bronzium Daily");
    aboutBoxTitleMap.put(TAB_BRONZIUM_ALLY_POINTS, "About Bronzium Ally Points");
    aboutBoxTitleMap.put(TAB_READ_UNEQUIPPED_MODS, "About Read Unequipped Mods");
    aboutBoxTitleMap.put(TAB_MOVE_MODS, "About Move Mods");
    aboutBoxTitleMap.put(TAB_REVERT_MOVE_MODS, "About Revert Move Mods");

    aboutBoxLayoutMap.put(TAB_BRONZIUM_DAILY, "/javafx/aboutBronziumDailyLayout.fxml");
    aboutBoxLayoutMap.put(TAB_BRONZIUM_ALLY_POINTS, "/javafx/aboutBronziumAllyPointsLayout.fxml");
    aboutBoxLayoutMap.put(TAB_READ_UNEQUIPPED_MODS, "/javafx/aboutReadUnequippedModsLayout.fxml");
    aboutBoxLayoutMap.put(TAB_MOVE_MODS, "/javafx/aboutMoveModsLayout.fxml");
    aboutBoxLayoutMap.put(TAB_REVERT_MOVE_MODS, "/javafx/aboutRevertMoveModsLayout.fxml");
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
    String activeTabId = getActiveTabId();
    String layout = aboutBoxLayoutMap.get(activeTabId);
    if (layout == null) {
      return;
    }

    TextFlow textFlow;
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(layout));
      textFlow = fxmlLoader.load();
    }
    catch (IOException e) {
      setErrorMessage("Unable to load about box: " + e.getMessage());
      return;
    }

    Stage aboutStage = new Stage();
    aboutStage.setTitle(aboutBoxTitleMap.get(activeTabId));

    Scene scene = new Scene(textFlow);
    aboutStage.setScene(scene);
    aboutStage.initModality(Modality.APPLICATION_MODAL);
    aboutStage.setResizable(false);

    aboutStage.show();
  }

  public void run() {
    Runnable runnable = featureMap.get(getActiveTabId());
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
    loadProgressFileImpl(progressFileName.getText(), allyCode);
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
    checkMoveModsFileImpl(moveModsFileName.getText());
  }

  public void runMoveMods() {
    String fileName = moveModsFileName.getText();
    boolean bDryRun = dryRun.isSelected();

    AbstractProcess process = new MoveMods();
    process.setFeedback(this);
    process.setParameters(fileName, String.valueOf(bDryRun));
    process.process();
  }

  public void chooseRevertMoveModsFile() {
    chooseFile(revertMoveModsFileName::setText);
  }

  public void checkRevertMoveModsFile() {
    checkMoveModsFileImpl(revertMoveModsFileName.getText());
  }

  public void chooseRevertProgressFile() {
    chooseFile(revertProgressFileName::setText);
  }

  public void loadRevertProgressFile() {
    loadProgressFileImpl(revertProgressFileName.getText(), revertAllyCode);
  }

  public void runRevertMoveMods() {
    String moveModsFileName = revertMoveModsFileName.getText();
    String progressFileName = revertProgressFileName.getText();
    String revertAlly = revertAllyCode.getValue();
    boolean bRevertDryRun = revertDryRun.isSelected();

    AbstractProcess process = new RevertMoveMods();
    process.setFeedback(this);
    process.setParameters(moveModsFileName, progressFileName, revertAlly, String.valueOf(bRevertDryRun));
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

  public void loadProgressFileImpl(String fileName, ComboBox<String> allyCodeComboBox) {
    try {
      Progress progress = JsonConnector.readProgressFromFile(fileName);
      allyCodeComboBox.getItems().clear();
      allyCodeComboBox.getItems().addAll(
              progress.getProfiles().stream().map(Profile::getAllyCode).collect(Collectors.toList())
      );
      if (allyCodeComboBox.getItems().size() == 1) {
        allyCodeComboBox.setValue(allyCodeComboBox.getItems().get(0));
      }
      setMessage("File succesfully loaded. Number of profiles: " + progress.getProfiles().size());
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
    }
  }

  public void checkMoveModsFileImpl(String fileName) {
    try {
      List<Mod> mods = HtmlConnector.getModsFromHTML(fileName);
      int numberOfMods = mods.size();
      long numberOfCharacters = mods.stream().map(Mod::getCharacter).distinct().count();
      setMessage("File succesfully loaded. Number of characters: " + numberOfCharacters + ", number of mods to move: " + numberOfMods);
    }
    catch (Exception e) {
      setErrorMessage("Error: " + e.getMessage());
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
      controls.setDisable(disabled);
    });
  }

}
