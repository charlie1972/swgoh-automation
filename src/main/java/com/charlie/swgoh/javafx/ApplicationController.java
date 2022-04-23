package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.AutomationSpeed;
import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.FeedbackStatus;
import com.charlie.swgoh.automation.IFeedback;
import com.charlie.swgoh.automation.process.*;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.MoveFile;
import com.charlie.swgoh.datamodel.json.MoveStatus;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.util.FileUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ApplicationController implements IFeedback {

  // From top to bottom
  // Tab pane
  @FXML
  private TabPane tabPane;

  // Bronzium Tab
  @FXML
  public Button bronziumsBtnDailyCollect;

  @FXML
  public TextField bronziumsTargetAllyPoints;

  @FXML
  public Button bronziumsBtnTargetCollect;

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

  // Speed selector
  @FXML
  private HBox controls;

  @FXML
  private ChoiceBox<String> speed;

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

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

  // Bronzium tab
  public void bronziumDailyCollect() {
    executorService.execute(() -> {
      Flasher flasher = new Flasher(bronziumsBtnDailyCollect);

      AbstractProcess process = new BronziumDaily();
      process.setFeedback(this);
      process.process();

      flasher.stop();
    });
  }

  public void bronziumsTargetCollect() {
    executorService.execute(() -> {
      Flasher flasher = new Flasher(bronziumsBtnTargetCollect);

      String target = bronziumsTargetAllyPoints.getText();
      AbstractProcess process = new BronziumAllyPoints(target);
      process.setFeedback(this);
      process.process();

      flasher.stop();
    });
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
      Flasher flasher = new Flasher(modsBtnReadUnequippedMods);

      String ally = modsAllyCode.getValue();
      String fileName = modsWorkingDirectory.getText() + File.separatorChar + modsProgressFile.getText();

      AbstractProcess process = new ReadUnequippedMods(ally, fileName);
      process.setFeedback(this);
      process.process();

      flasher.stop();
      modsRefresh();
    });
  }

  public void modsMoveSelected() {
    executorService.execute(() -> {
      Flasher flasher = new Flasher(modsBtnMoveSelected);

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

      flasher.stop();
      modsRefresh();
    });
  }

  public void modsRevertSelected() {
    executorService.execute(() -> {
      Flasher flasher = new Flasher(modsBtnRevertSelected);

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

      flasher.stop();
      modsRefresh();
    });
  }

  public void modsRevertAll() {
    executorService.execute(() -> {
      Flasher flasher = new Flasher(modsBtnRevertAll);

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

      flasher.stop();
      modsRefresh();
    });
  }

  private static class Flasher {
    private final Timeline timeline;
    private final Node node;
    private final String nodeStyle;
    private final String nodeHighlightedStyle;

    Flasher(Node node) {
      this.node = node;
      nodeStyle = node.getStyle();
      nodeHighlightedStyle = "-fx-background-color: #bbffbb; " + this.nodeStyle;
      timeline = new Timeline(
              new KeyFrame(Duration.seconds(0.5), e -> highlightNode()),
              new KeyFrame(Duration.seconds(1.0), e -> unHighlightNode())
      );
      timeline.setCycleCount(Animation.INDEFINITE);
      timeline.play();
    }

    void stop() {
      timeline.stop();
      unHighlightNode();
    }

    private void highlightNode() {
      node.setStyle(nodeHighlightedStyle);
    }

    private void unHighlightNode() {
      node.setStyle(nodeStyle);
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
