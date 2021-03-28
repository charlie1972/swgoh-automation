package com.charlie.swgoh.main;

import com.charlie.swgoh.javafx.ApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXApp extends Application {

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/javafx/applicationLayout.fxml"));
    VBox vbox = fxmlLoader.load();
    fxmlLoader.<ApplicationController>getController().setPrimaryStage(primaryStage);

    primaryStage.setTitle("Automation tool for Star Wars: Galaxy of Heroes");
    Scene scene = new Scene(vbox);
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);

    primaryStage.show();
  }

  @Override
  public void stop() {
    System.exit(0);
  }

}
