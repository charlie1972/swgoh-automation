package com.charlie.swgoh.datamodel;

import java.util.stream.Stream;

public enum ModStatUnit {
  ACCURACY_PCT("% Accuracy", "% Accuracy", "Accuracy %"),
  CRIT_AVOIDANCE_PCT("% Crit Avoidance", "% Critical Avoidance", "Critical Avoidance %"),
  CRIT_CHANCE_PCT("% Crit Chance", "% Critical Chance", "Critical Chance %"),
  CRIT_DAMAGE("% Crit Damage", "% Critical Damage", "Critical Damage %"),
  DEFENSE_FLAT(" Defense", " Defense", "Defense"),
  DEFENSE_PCT("% Defense", "% Defense", "Defense %"),
  HEALTH_FLAT(" Health", " Health", "Health"),
  HEALTH_PCT("% Health", "% Health", "Health %"),
  OFFENSE_FLAT(" Offense", " Offense", "Offense"),
  OFFENSE_PCT("% Offense", "% Offense", "Offense %"),
  POTENCY("% Potency", "% Potency", "Potency %"),
  PROTECTION_FLAT(" Protection", " Protection", "Protection"),
  PROTECTION_PCT("% Protection", "% Protection", "Protection %"),
  SPEED(" Speed", " Speed", "Speed"),
  TENACITY("% Tenacity", "% Tenacity", "Tenacity %");

  private final String gameText;
  private final String optimizerXmlText;
  private final String optimizerJsonText;

  ModStatUnit(String gameText, String optimizerXmlText, String optimizerJsonText) {
    this.gameText = gameText;
    this.optimizerXmlText = optimizerXmlText;
    this.optimizerJsonText = optimizerJsonText;
  }

  public static ModStatUnit fromString(String text) {
    return Stream.of(ModStatUnit.values())
            .filter(modStatUnit -> Stream.of(modStatUnit.gameText, modStatUnit.optimizerXmlText).anyMatch(
                    modStatUnitText -> matches(modStatUnitText, text)
            ))
            .findFirst()
            .orElse(null);
  }

  @Override
  public String toString() {
    return gameText;
  }

  public String toJsonString() {
    return optimizerJsonText;
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
