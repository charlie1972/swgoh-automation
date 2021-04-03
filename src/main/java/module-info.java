module swgoh.automation {
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires java.xml.bind;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires com.sun.jna;
  requires com.sun.jna.platform;
  requires sikulixapi;
  requires slf4j.api;
  requires java.desktop;
  requires fuzzywuzzy;
  requires Saxon.HE;

  opens com.charlie.swgoh.main;
  opens com.charlie.swgoh.javafx;
  opens com.charlie.swgoh.datamodel;
  opens com.charlie.swgoh.datamodel.json;
  opens com.charlie.swgoh.datamodel.xml;
  opens com.charlie.swgoh.connector;
}
