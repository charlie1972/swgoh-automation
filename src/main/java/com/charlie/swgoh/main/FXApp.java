package com.charlie.swgoh.main;

import com.charlie.swgoh.javafx.FXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class FXApp extends Application {

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/javafx/layout.fxml"));
    TabPane tabPane = fxmlLoader.load();
    fxmlLoader.<FXController>getController().setPrimaryStage(primaryStage);

    primaryStage.setTitle("Automation tool for Star Wars: Galaxy of Heroes");
    Scene scene = new Scene(tabPane, 600, 400);
    primaryStage.setScene(scene);

    primaryStage.show();
  }

  @Override
  public void stop() {
    System.exit(0);
  }

}
