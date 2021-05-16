package com.charlie.swgoh.datamodel;

import jakarta.xml.bind.annotation.XmlEnumValue;

public enum ModTier {

  @XmlEnumValue("gray")
  E(1),

  @XmlEnumValue("green")
  D(2),

  @XmlEnumValue("blue")
  C(3),

  @XmlEnumValue("purple")
  B(4),

  @XmlEnumValue("gold")
  A(5);

  private final int jsonInt;

  ModTier(int jsonInt) {
    this.jsonInt = jsonInt;
  }

  public int toJsonInt() {
    return jsonInt;
  }

  public static ModTier fromJsonInt(int theInt) {
    for (ModTier modTier : ModTier.values()) {
      if (modTier.jsonInt == theInt) {
        return modTier;
      }
    }
    return null;
  }

}
