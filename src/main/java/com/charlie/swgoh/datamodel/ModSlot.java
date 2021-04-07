package com.charlie.swgoh.datamodel;

import jakarta.xml.bind.annotation.XmlEnumValue;

public enum ModSlot {

  @XmlEnumValue("square")
  SQUARE("square"),

  @XmlEnumValue("arrow")
  ARROW("arrow"),

  @XmlEnumValue("diamond")
  DIAMOND("diamond"),

  @XmlEnumValue("triangle")
  TRIANGLE("triangle"),

  @XmlEnumValue("circle")
  CIRCLE("circle"),

  @XmlEnumValue("cross")
  CROSS("cross");

  private final String jsonString;

  ModSlot(String jsonString) {
    this.jsonString = jsonString;
  }

  public String toJsonString() {
    return jsonString;
  }

}
