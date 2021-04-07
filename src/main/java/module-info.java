module swgoh.automation {
  // Modules
  requires java.desktop;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires jakarta.xml.bind;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.sun.jna;
  requires com.sun.jna.platform;
  requires org.slf4j;

  // Automatic modules
  requires sikulixapi;
  requires fuzzywuzzy;
  requires Saxon.HE;

  // Openings for reflection
  opens com.charlie.swgoh.main;
  opens com.charlie.swgoh.javafx;
  opens com.charlie.swgoh.datamodel;
  opens com.charlie.swgoh.datamodel.json;
  opens com.charlie.swgoh.datamodel.xml;
  opens com.charlie.swgoh.connector;
}
