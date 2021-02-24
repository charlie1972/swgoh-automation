package com.charlie.swgoh.datamodel;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
public enum ModSet {

  @XmlEnumValue("health")
  HEALTH("health"),

  @XmlEnumValue("defense")
  DEFENSE("defense"),

  @XmlEnumValue("critdamage")
  CRIT_DAMAGE("critdamage"),

  @XmlEnumValue("critchance")
  CRIT_CHANCE("critchance"),

  @XmlEnumValue("tenacity")
  TENACITY("tenacity"),

  @XmlEnumValue("offense")
  OFFENSE("offense"),

  @XmlEnumValue("potency")
  POTENCY("potency"),

  @XmlEnumValue("speed")
  SPEED("speed");

  private final String jsonString;

  ModSet(String jsonString) {
    this.jsonString = jsonString;
  }

  public String toJsonString() {
    return jsonString;
  }

}
