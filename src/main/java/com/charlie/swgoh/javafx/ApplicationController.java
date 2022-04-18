package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.AutomationSpeed;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.FeedbackStatus;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.automation.process.*;
import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.MoveFile;
import com.charlie.swgoh.datamodel.json.MoveStatus;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.util.FileUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

  // Mods Tab
  @FXML
  public TextField modsWorkingDirectory;

  @FXML
  public Label modsProgressFile;

  @FXML
  public Label modsEnrichedProgressFile;

  @FXML
  public ComboBox<String> modsAllyCode;

  @FXML
  public TableView<MoveFile> modsMoveFiles;

  @FXML
  public CheckBox modsDryRun;

  @FXML
  public Button modsBtnReadUnequippedMods;

  @FXML
  public Button modsBtnMoveSelected;

  @FXML
  public Button modsBtnRevertSelected;

  @FXML
  public Button modsBtnRevertAll;

  // Status zone
  @FXML
  private Label status;

  @FXML
  private Label message;

  @FXML
  private ProgressBar progress;

  @FXML
  private Label eta;

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

    modsMoveFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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

    AbstractProcess process = new ReadUnequippedMods(ally, fileName);
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
/*
    String fileName = moveModsFileName.getText();
    boolean bDryRun = dryRun.isSelected();

    AbstractProcess process = new MoveMods();
    process.setFeedback(this);
    process.setParameters(fileName, String.valueOf(bDryRun));
    process.process();
*/
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
/*
    String moveModsFileName = revertMoveModsFileName.getText();
    String progressFileName = revertProgressFileName.getText();
    String revertAlly = revertAllyCode.getValue();
    boolean bRevertDryRun = revertDryRun.isSelected();

    AbstractProcess process = new RevertMoveMods();
    process.setFeedback(this);
    process.setParameters(progressFileName, revertAlly, String.valueOf(bRevertDryRun), moveModsFileName);
    process.process();
*/
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
      Progress progress = JsonConnector.readObjectFromFile(fileName, Progress.class);
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

  // Mod tab
  public void modsChooseDirectory() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    String defaultDirectory = Configuration.getDefaultDirectory();
    if (!defaultDirectory.isEmpty()) {
      directoryChooser.setInitialDirectory(new File(defaultDirectory));
    }
    File directory = directoryChooser.showDialog(primaryStage);
    if (directory != null ) {
      String fullPath = directory.getAbsolutePath();
      Configuration.setDefaultDirectory(fullPath);
      modsWorkingDirectory.setText(fullPath);
      modsRefresh();
    }
  }

  public void modsRefresh() {
    Platform.runLater(() -> {
      String workingDirectory = modsWorkingDirectory.getText();

      String allyCode = modsAllyCode.getValue();

      // Clear all
      modsProgressFile.setText("");
      modsEnrichedProgressFile.setText("");
      modsAllyCode.getItems().clear();
      modsMoveFiles.getItems().clear();

      // Gets all files in directory
      List<FileUtil.FileComponents> fileComponents;
      try {
        fileComponents = FileUtil.getFilesInDirectory(workingDirectory);
      }
      catch (Exception e) {
        setErrorMessage("Error reading working directory: " + e.getMessage());
        return;
      }

      // Separate files
      List<FileUtil.FileComponents> progressFiles = fileComponents.stream()
              .filter(fileComponent -> fileComponent.getExtension().equals("json") && fileComponent.getFileName().startsWith("modsOptimizer"))
              .collect(Collectors.toList());
      List<FileUtil.FileComponents> enrichedProgressFiles = fileComponents.stream()
              .filter(fileComponent -> fileComponent.getExtension().equals("json") && fileComponent.getFileName().startsWith("enriched-modsOptimizer"))
              .collect(Collectors.toList());
      List<FileUtil.FileComponents> moveModsFiles = fileComponents.stream()
              .filter(fileComponent -> fileComponent.getExtension().startsWith("htm"))
              .collect(Collectors.toList());

      // Progress file
      if (progressFiles.isEmpty()) {
        setErrorMessage("Error: No progress file");
        return;
      }
      if (progressFiles.size() > 1) {
        setErrorMessage("Error: There is more than one progress file");
        return;
      }
      modsProgressFile.setText(progressFiles.get(0).getFileAndExtension());

      // Enriched progress file
      if (enrichedProgressFiles.size() > 1) {
        setErrorMessage("Error: There is more than one enriched progress file");
        return;
      }
      if (!enrichedProgressFiles.isEmpty()) {
        modsEnrichedProgressFile.setText(enrichedProgressFiles.get(0).getFileAndExtension());
      }

      // Ally code
      try {
        Progress progress = JsonConnector.readObjectFromFile(progressFiles.get(0).toString(), Progress.class);
        List<String> allyCodesInProfile = progress.getProfiles().stream().map(Profile::getAllyCode).collect(Collectors.toList());
        modsAllyCode.getItems().addAll(allyCodesInProfile);
        if (allyCodesInProfile.size() == 1) {
          modsAllyCode.setValue(allyCodesInProfile.get(0));
        }
        else if (allyCodesInProfile.contains(allyCode)) {
          modsAllyCode.setValue(allyCode);
        }
        else {
          modsAllyCode.setValue("");
        }
      }
      catch (Exception e) {
        setErrorMessage("Error reading progress file: " + e.getMessage());
        return;
      }

      // Move files
      String revertAllStatusFile = new FileUtil.FileComponents(workingDirectory, "__all__-revert", "status").toString();
      if (FileUtil.exists(revertAllStatusFile)) {
        MoveFile.Status tempStatus;
        try {
          MoveStatus revertAllStatus = JsonConnector.readObjectFromFile(revertAllStatusFile, MoveStatus.class);
          if (revertAllStatus.getAttention().isEmpty() && revertAllStatus.getToProcess().isEmpty()) {
            tempStatus = MoveFile.Status.REVERTED_ALL;
          } else {
            tempStatus = MoveFile.Status.REVERTING_ALL;
          }
        } catch (Exception e) {
          tempStatus = MoveFile.Status.UNKNOWN;
        }
        MoveFile.Status status = tempStatus;
        moveModsFiles.stream()
                .sorted(Comparator.comparing(FileUtil.FileComponents::getFileName))
                .forEach(fc -> modsMoveFiles.getItems().add(new MoveFile(fc, status)));
      }
      else {
        moveModsFiles.stream()
                .sorted(Comparator.comparing(FileUtil.FileComponents::getFileName))
                .map(fc -> {
                  String moveStatusFile = fc
                          .withFileName(fc.getFileName() + "-move")
                          .withExtension("status")
                          .toString();
                  String revertStatusFile = fc
                          .withFileName(fc.getFileName() + "-revert")
                          .withExtension("status")
                          .toString();
                  boolean hasMove = FileUtil.exists(moveStatusFile);
                  boolean hasRevert = FileUtil.exists(revertStatusFile);
                  if (!hasRevert) {
                    if (!hasMove) {
                      return new MoveFile(fc, MoveFile.Status.NEW);
                    } else {
                      try {
                        MoveStatus moveStatus = JsonConnector.readObjectFromFile(moveStatusFile, MoveStatus.class);
                        if (moveStatus.getAttention().isEmpty() && moveStatus.getToProcess().isEmpty()) {
                          return new MoveFile(fc, MoveFile.Status.MOVED);
                        } else {
                          return new MoveFile(fc, MoveFile.Status.MOVING);
                        }
                      } catch (Exception e) {
                        return new MoveFile(fc, MoveFile.Status.UNKNOWN);
                      }
                    }
                  } else {
                    if (hasMove) {
                      try {
                        MoveStatus revertStatus = JsonConnector.readObjectFromFile(revertStatusFile, MoveStatus.class);
                        if (revertStatus.getAttention().isEmpty() && revertStatus.getToProcess().isEmpty()) {
                          return new MoveFile(fc, MoveFile.Status.REVERTED);
                        } else {
                          return new MoveFile(fc, MoveFile.Status.REVERTING);
                        }
                      } catch (Exception e) {
                        return new MoveFile(fc, MoveFile.Status.UNKNOWN);
                      }
                    } else {
                      return new MoveFile(fc, MoveFile.Status.UNKNOWN);
                    }
                  }
                })
                .forEach(moveFile -> modsMoveFiles.getItems().add(moveFile));
      }
    });
  }

  public void modsReadUnequippedMods() {
    executorService.execute(() -> {
      setButtonHighlight(modsBtnReadUnequippedMods, true);

      String ally = modsAllyCode.getValue();
      String fileName = modsWorkingDirectory.getText() + File.separatorChar + modsProgressFile.getText();

      AbstractProcess process = new ReadUnequippedMods(ally, fileName);
      process.setFeedback(this);
      process.process();

      setButtonHighlight(modsBtnReadUnequippedMods, false);
      modsRefresh();
    });
  }

  public void modsMoveSelected() {
    executorService.execute(() -> {
      setButtonHighlight(modsBtnMoveSelected, true);

      ObservableList<MoveFile> selectedItems = modsMoveFiles.getSelectionModel().getSelectedItems();
      if (selectedItems.isEmpty()) {
        setErrorMessage("No mods move file selected");
        return;
      }

      String fileName = selectedItems.get(0).getFileComponents().toString();
      boolean bDryRun = modsDryRun.isSelected();

      AbstractProcess process = new MoveMods(fileName, bDryRun);
      process.setFeedback(this);
      process.process();

      setButtonHighlight(modsBtnMoveSelected, false);
      modsRefresh();
    });
  }

  public void modsRevertSelected() {
    executorService.execute(() -> {
      setButtonHighlight(modsBtnRevertSelected, true);

      ObservableList<MoveFile> selectedItems = modsMoveFiles.getSelectionModel().getSelectedItems();
      if (selectedItems.isEmpty()) {
        setErrorMessage("No mods move file selected");
        return;
      }

      String moveModsFileName = selectedItems.get(0).getFileComponents().toString();
      String progressFileName = modsWorkingDirectory.getText() + File.separatorChar + modsProgressFile.getText();
      String revertAlly = modsAllyCode.getValue();
      boolean bRevertDryRun = modsDryRun.isSelected();

      AbstractProcess process = new RevertMoveMods(
              Collections.singletonList(moveModsFileName),
              progressFileName,
              revertAlly,
              bRevertDryRun
      );
      process.setFeedback(this);
      process.process();

      setButtonHighlight(modsBtnRevertSelected, false);
      modsRefresh();
    });
  }

  public void modsRevertAll() {
    executorService.execute(() -> {
      setButtonHighlight(modsBtnRevertAll, true);

      ObservableList<MoveFile> allItems = modsMoveFiles.getItems();
      if (allItems.isEmpty()) {
        setErrorMessage("No mods move file");
        return;
      }

      String progressFileName = modsWorkingDirectory.getText() + File.separatorChar + modsProgressFile.getText();
      String revertAlly = modsAllyCode.getValue();
      boolean bRevertDryRun = modsDryRun.isSelected();

      AbstractProcess process = new RevertMoveMods(
              allItems.stream().map(MoveFile::getFileComponents).map(Object::toString).collect(Collectors.toList()),
              progressFileName,
              revertAlly,
              bRevertDryRun
      );
      process.setFeedback(this);
      process.process();

      setButtonHighlight(modsBtnRevertAll, false);
      modsRefresh();
    });
  }

  private void setButtonHighlight(Button button, boolean isHighlighted) {
    String style = isHighlighted ? "-fx-background-color: #00ff00;" : "";
    Platform.runLater(() -> button.setStyle(style));
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
  public void setETA(String etaString) {
    Platform.runLater(() -> eta.setText(etaString));
  }

  @Override
  public void setAllControlsDisabled(boolean disabled) {
    Platform.runLater(() -> {
      tabPane.setDisable(disabled);
      controls.setDisable(disabled);
    });
  }

}
