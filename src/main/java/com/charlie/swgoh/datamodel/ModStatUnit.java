package com.charlie.swgoh.datamodel;

import com.charlie.swgoh.util.StringUtil;

public enum ModStatUnit {
  ACCURACY_PCT("% Accuracy", "%Accuracy", "Accuracy %"),
  CRIT_AVOIDANCE_PCT("% Crit Avoidance", "%CriticalAvoidance", "Critical Avoidance %"),
  CRIT_CHANCE_PCT("% Crit Chance", "%CriticalChance", "Critical Chance %"),
  CRIT_DAMAGE("% Crit Damage", "%CriticalDamage", "Critical Damage %"),
  DEFENSE_FLAT(" Defense", "Defense", "Defense"),
  DEFENSE_PCT("% Defense", "%Defense", "Defense %"),
  HEALTH_FLAT(" Health", "Health", "Health"),
  HEALTH_PCT("% Health", "%Health", "Health %"),
  OFFENSE_FLAT(" Offense", "Offense", "Offense"),
  OFFENSE_PCT("% Offense", "%Offense", "Offense %"),
  POTENCY("% Potency", "%Potency", "Potency %"),
  PROTECTION_FLAT(" Protection", "Protection", "Protection"),
  PROTECTION_PCT("% Protection", "%Protection", "Protection %"),
  SPEED(" Speed", "Speed", "Speed"),
  TENACITY("% Tenacity", "%Tenacity", "Tenacity %");

  private final String prettyText;
  private final String gameText;
  private final String optimizerXmlText;
  private final String optimizerJsonText;

  ModStatUnit(String prettyText, String optimizerXmlText, String optimizerJsonText) {
    this.prettyText = prettyText;
    this.gameText = StringUtil.stripSpaces(prettyText);
    this.optimizerXmlText = optimizerXmlText;
    this.optimizerJsonText = optimizerJsonText;
  }

  public static ModStatUnit fromString(String text, InputType inputType) {
    // First, try strict matching
    for (ModStatUnit modStatUnit : ModStatUnit.values()) {
      if (inputType.getModStatUnitText().apply(modStatUnit).equalsIgnoreCase(text)) {
        return modStatUnit;
      }
    }
    // If it fails, try fuzzy matching
    for (ModStatUnit modStatUnit : ModStatUnit.values()) {
      if (StringUtil.fuzzyMatch(inputType.getModStatUnitText().apply(modStatUnit), text)) {
        return modStatUnit;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return prettyText;
  }

  public String getJsonString() {
    return optimizerJsonText;
  }

  public String getGameText() {
    return gameText;
  }

  public String getOptimizerXmlText() {
    return optimizerXmlText;
  }

  private static boolean matches(String text1, String text2) {
    if (text1 == null || text2 == null) {
      return false;
    }
    if (text2.startsWith(text1) || text1.startsWith(text2)) {
      return true;
    }

    String t1 = (text1.length() < 12) ? text1 : text1.substring(0, 12);
    String t2 = (text2.length() < 12) ? text2 : text2.substring(0, 12);
    return t1.equals(t2);
  }

}
