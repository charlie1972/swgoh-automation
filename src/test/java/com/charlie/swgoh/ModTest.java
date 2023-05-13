package com.charlie.swgoh;

import com.charlie.swgoh.datamodel.InputType;
import com.charlie.swgoh.datamodel.ModStat;
import com.charlie.swgoh.datamodel.ModStatUnit;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.charlie.swgoh.datamodel.ModStatUnit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModTest {
  
  private static class ModStatData {
    final String text;
    final int rolls;
    final String value;
    final ModStatUnit unit;

    public ModStatData(String text, int rolls, String value, ModStatUnit unit) {
      this.text = text;
      this.rolls = rolls;
      this.value = value;
      this.unit = unit;
    }
  }
  
  @Test
  public void testModStat() {

    Stream.of(
            new ModStatData("15% Accuracy", 0, "15", ACCURACY_PCT),
            new ModStatData("15% Crit Avoidance", 0, "15", CRIT_AVOIDANCE_PCT),
            new ModStatData("15% Crit Chance", 0, "15", CRIT_CHANCE_PCT),
            new ModStatData("15% Crit Damage", 0, "15", CRIT_DAMAGE),
            new ModStatData("15 Defense", 0, "15", DEFENSE_FLAT),
            new ModStatData("15% Defense", 0, "15", DEFENSE_PCT),
            new ModStatData("15 Health", 0, "15", HEALTH_FLAT),
            new ModStatData("15% Health", 0, "15", HEALTH_PCT),
            new ModStatData("15 Offense", 0, "15", OFFENSE_FLAT),
            new ModStatData("15% Offense", 0, "15", OFFENSE_PCT),
            new ModStatData("15% Potency", 0, "15", POTENCY),
            new ModStatData("15 Protection", 0, "15", PROTECTION_FLAT),
            new ModStatData("15% Protection", 0, "15", PROTECTION_PCT),
            new ModStatData("15 Speed", 0, "15", SPEED),
            new ModStatData("15% Tenacity", 0, "15", TENACITY),
            new ModStatData("1.54% Crit Chance", 0, "1.54", CRIT_CHANCE_PCT),
            new ModStatData("1.54% Defense", 0, "1.54", DEFENSE_PCT),
            new ModStatData("1.54% Health", 0, "1.54", HEALTH_PCT),
            new ModStatData("1.54% Offense", 0, "1.54", OFFENSE_PCT),
            new ModStatData("1.54% Potency", 0, "1.54", POTENCY),
            new ModStatData("1.54% Protection", 0, "1.54", PROTECTION_PCT),
            new ModStatData("1.54% Tenacity", 0, "1.54", TENACITY),
            new ModStatData("(1) 15% Accuracy", 1, "15", ACCURACY_PCT),
            new ModStatData("(2) 15% Crit Avoidance", 2, "15", CRIT_AVOIDANCE_PCT),
            new ModStatData("(3) 15% Crit Chance", 3, "15", CRIT_CHANCE_PCT),
            new ModStatData("(4) 15% Crit Damage", 4, "15", CRIT_DAMAGE),
            new ModStatData("(5) 15 Defense", 5, "15", DEFENSE_FLAT),
            new ModStatData("(1) 15% Defense", 1, "15", DEFENSE_PCT),
            new ModStatData("(2) 15 Health", 2, "15", HEALTH_FLAT),
            new ModStatData("(3) 15% Health", 3, "15", HEALTH_PCT),
            new ModStatData("(4) 15 Offense", 4, "15", OFFENSE_FLAT),
            new ModStatData("(5) 15% Offense", 5, "15", OFFENSE_PCT),
            new ModStatData("(1) 15% Potency", 1, "15", POTENCY),
            new ModStatData("(2) 15 Protection", 2, "15", PROTECTION_FLAT),
            new ModStatData("(3) 15% Protection", 3, "15", PROTECTION_PCT),
            new ModStatData("(4) 15 Speed", 4, "15", SPEED),
            new ModStatData("(5) 15% Tenacity", 5, "15", TENACITY),
            new ModStatData("(1) 1.54% Crit Chance", 1, "1.54", CRIT_CHANCE_PCT),
            new ModStatData("(2) 1.54% Defense", 2, "1.54", DEFENSE_PCT),
            new ModStatData("(3) 1.54% Health", 3, "1.54", HEALTH_PCT),
            new ModStatData("(4) 1.54% Offense", 4, "1.54", OFFENSE_PCT),
            new ModStatData("(5) 1.54% Potency", 5, "1.54", POTENCY),
            new ModStatData("(1) 1.54% Protection", 1, "1.54", PROTECTION_PCT),
            new ModStatData("(2) 1.54% Tenacity", 2, "1.54", TENACITY)
    ).forEach(
            modStatData -> {
              // Test toString()
              assertEquals(modStatData.text, new ModStat(modStatData.rolls, modStatData.value, modStatData.unit).toString());
              // Test parsing constructor
              assertEquals(new ModStat(modStatData.rolls, modStatData.value, modStatData.unit), new ModStat(modStatData.text, InputType.GAME));
            }
    );

  }

}
