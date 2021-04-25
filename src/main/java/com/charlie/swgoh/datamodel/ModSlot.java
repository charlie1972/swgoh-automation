package com.charlie.swgoh.datamodel;

import jakarta.xml.bind.annotation.XmlEnumValue;

public enum ModSlot {

  @XmlEnumValue("square")
  SQUARE("square", "TRANSMITTER"),

  @XmlEnumValue("arrow")
  ARROW("arrow", "RECEIVER"),

  @XmlEnumValue("diamond")
  DIAMOND("diamond", "PROCESSOR"),

  @XmlEnumValue("triangle")
  TRIANGLE("triangle", "HOLO-ARRAY"),

  @XmlEnumValue("circle")
  CIRCLE("circle", "DATA-BUS"),

  @XmlEnumValue("cross")
  CROSS("cross", "MULTIPLEXER");

  private final String shape;
  private final String name;

  ModSlot(String shape, String name) {
    this.shape = shape;
    this.name = name;
  }

  public String toJsonString() {
    return shape;
  }

  public static ModSlot fromName(String theName) {
    for (ModSlot modSlot : ModSlot.values()) {
      if (modSlot.name.equals(theName)) {
        return modSlot;
      }
    }
    return null;
  }

}
