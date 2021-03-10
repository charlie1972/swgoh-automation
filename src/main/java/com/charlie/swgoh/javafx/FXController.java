package com.charlie.swgoh.javafx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXController {

  private static final Logger LOG = LoggerFactory.getLogger(FXController.class);

  public void aboutBronziumDaily() {
    LOG.info("About Bronzium Daity");
    showAboutDialog("About Bronzium Daily", "First Line\nSecond Line");
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

}
