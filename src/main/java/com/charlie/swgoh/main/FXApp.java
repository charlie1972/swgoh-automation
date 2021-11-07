package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.javafx.ApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXApp extends Application {

  private static boolean isDebug;

  public static void main(String[] args) {
    isDebug = args.length > 0 && "debug".equalsIgnoreCase(args[0]);
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/javafx/applicationLayout.fxml"));
    VBox vbox = fxmlLoader.load();
    ApplicationController controller = fxmlLoader.getController();

    primaryStage.setTitle("Automation tool for Star Wars: Galaxy of Heroes");
    Scene scene = new Scene(vbox);
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);

    controller.setPrimaryStage(primaryStage);
    controller.init(isDebug);

    primaryStage.show();
  }

  @Override
  public void stop() {
    Configuration.saveProperties();
    System.exit(0);
  }

}
