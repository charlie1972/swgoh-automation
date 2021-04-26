package com.charlie.swgoh.datamodel;

import com.charlie.swgoh.util.AutomationUtil;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

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

  private final String text;

  ModSet(String text) {
    this.text = text;
  }

  public String toJsonString() {
    return text;
  }

  public static ModSet fromString(String s) {
    if (s == null) {
      return null;
    }
    String stripped = AutomationUtil.stripSpaces(s);
    for (ModSet modSet : ModSet.values()) {
      if (modSet.text.equalsIgnoreCase(stripped)) {
        return modSet;
      }
    }
    return null;
  }

}
