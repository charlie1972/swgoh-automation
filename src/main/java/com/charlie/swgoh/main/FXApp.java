package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.javafx.ApplicationController;
import com.charlie.swgoh.javafx.DebugController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXApp extends Application {

  public static void main(String[] args) {
    Configuration.setDebug(args.length > 0 && "debug".equalsIgnoreCase(args[0]));
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader mainFxmlLoader = new FXMLLoader(this.getClass().getResource("/javafx/applicationLayout.fxml"));
    VBox mainVbox = mainFxmlLoader.load();
    ApplicationController mainController = mainFxmlLoader.getController();

    primaryStage.setTitle("Automation tool for Star Wars: Galaxy of Heroes");
    Scene mainScene = new Scene(mainVbox);
    primaryStage.setScene(mainScene);
    primaryStage.setResizable(false);

    mainController.setPrimaryStage(primaryStage);
    mainController.init();

    primaryStage.show();

    if (Configuration.isDebug()) {
      FXMLLoader debugFxmlLoader = new FXMLLoader(this.getClass().getResource("/javafx/debugLayout.fxml"));
      VBox debugVbox = debugFxmlLoader.load();
      Stage debugStage = new Stage();
      debugStage.setTitle("Debug - use with care");
      Scene debugScene = new Scene(debugVbox);
      debugStage.setScene(debugScene);
      debugStage.setResizable(false);
      DebugController debugController = debugFxmlLoader.getController();
      debugController.init(primaryStage, debugStage);
      debugStage.show();
    }

  }

  @Override
  public void stop() {
    Configuration.saveProperties();
    System.exit(0);
  }

}
