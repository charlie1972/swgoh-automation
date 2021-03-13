package com.charlie.swgoh.javafx;

import com.charlie.swgoh.automation.process.IProcess;
import com.charlie.swgoh.automation.process.MainProcess;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FXController {

  private static final Logger LOG = LoggerFactory.getLogger(FXController.class);

  @FXML
  private TextField targetAllyPoints;

  @FXML
  private TextField allyCode;

  @FXML
  private TextField progressFileName;

  @FXML
  private TextField moveModsFileName;

  private Stage primaryStage;

  public void aboutBronziumDaily() {
    showAboutDialog("About Bronzium Daily", "Bronzium\nDaily");
  }

  public void runBronziumDaily() throws Exception {
    IProcess mainProcess = new MainProcess();
    mainProcess.setParameters(new String[]{"BronziumDaily"});
    mainProcess.process();
  }

  public void aboutBronziumAllyPoints() {
    showAboutDialog("About Bronzium Ally Points", "Bronzium\n\nAlly Points");
  }

  public void runBronziumAllyPoints() throws Exception {
    String target = targetAllyPoints.getText();

    IProcess mainProcess = new MainProcess();
    mainProcess.setParameters(new String[]{"BronziumAllyPoints", target});
    mainProcess.process();
  }

  public void aboutReadUnequippedMods() {
    showAboutDialog("About Read Unequipped Mods", "Read Unequipped Mods");
  }

  public void chooseProgressFile() {
    FileChooser fileChooser = new FileChooser();
    File progressFile = fileChooser.showOpenDialog(primaryStage);
    if (progressFile != null) {
      progressFileName.setText(progressFile.getAbsolutePath());
    }
  }

  public void runReadUnequippedMods() throws Exception {
    String ally = allyCode.getText();
    String fileName = progressFileName.getText();

    IProcess mainProcess = new MainProcess();
    mainProcess.setParameters(new String[]{"ReadUnequippedMods", ally, fileName});
    mainProcess.process();
  }

  public void aboutMoveMods() {
    showAboutDialog("About Move Mods", "Move Mods");
  }

  public void chooseMoveModsFile() {
    FileChooser fileChooser = new FileChooser();
    File moveModsFile = fileChooser.showOpenDialog(primaryStage);
    if (moveModsFile != null) {
      moveModsFileName.setText(moveModsFile.getAbsolutePath());
    }
  }

  public void runMoveMods() throws Exception {
    String fileName = moveModsFileName.getText();

    IProcess mainProcess = new MainProcess();
    mainProcess.setParameters(new String[]{"MoveMods", fileName});
    mainProcess.process();
  }

  private void showAboutDialog(String title, String description) {
    Text text = new Text(description);
    VBox vbox = new VBox(text);
    vbox.setPadding(new Insets(20));
    Scene scene = new Scene(vbox);
    Stage aboutStage = new Stage();
    aboutStage.initModality(Modality.APPLICATION_MODAL);
    aboutStage.setTitle(title);
    aboutStage.setScene(scene);

    aboutStage.showAndWait();
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

}
