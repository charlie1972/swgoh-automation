package com.charlie.swgoh.datamodel;

import javax.xml.bind.annotation.XmlEnumValue;

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

}
