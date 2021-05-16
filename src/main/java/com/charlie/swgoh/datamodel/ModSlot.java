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

  public String getShape() {
    return shape;
  }

  public String getName() {
    return name;
  }

  public static ModSlot fromString(String theString, InputType type) {
    for (ModSlot modSlot : ModSlot.values()) {
      if (type.getModSlotText().apply(modSlot).equals(theString)) {
        return modSlot;
      }
    }
    return null;
  }

}
