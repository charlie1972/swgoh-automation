package com.charlie.swgoh.main;

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

    primaryStage.setTitle("Automation tool for Star Wars: Galaxy of Heroes");
    Scene scene = new Scene(tabPane, 600, 200);
    //scene.getStylesheets().add("javafx/styles.css");
    primaryStage.setScene(scene);

    primaryStage.show();
  }

}
