package com.charlie.swgoh.datamodel;

import com.charlie.swgoh.util.ModUtil;
import com.charlie.swgoh.util.StringUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModStat {

  private final int rolls;
  private final double value;
  private final ModStatUnit unit;
  private String text = null;

  private static final Pattern PATTERN = Pattern.compile("(\\((\\d)\\))?(\\+?\\d+\\.?\\d*)(%?[A-Za-z]*).*?");
  private static final double TOLERANCE = 0.02;

  public ModStat(int rolls, double value, ModStatUnit unit) {
    this.rolls = rolls;
    this.value = value;
    this.unit = unit;
  }

  public ModStat(int rolls, String value, ModStatUnit unit) {
    this(rolls, Double.parseDouble(value), unit);
  }

  public ModStat(String str, InputType inputType) {
    String str2 = StringUtil.removeInvalidCharacters(str);
    Matcher matcher = PATTERN.matcher(str2);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(str + " is an invalid mod stat");
    }
    this.rolls = (matcher.group(1) == null || matcher.group(1).isEmpty()) ? 0 : Integer.parseInt(matcher.group(2));
    this.value = Double.parseDouble(matcher.group(3));
    this.unit = ModStatUnit.fromString(matcher.group(4), inputType);
  }

  public int getRolls() {
    return rolls;
  }

  public double getValue() {
    return value;
  }

  public ModStatUnit getUnit() {
    return unit;
  }

  @Override
  public String toString() {
    if (this.text == null) {
      this.text = rolls == 0 ? ModUtil.FORMAT.format(value) + unit : "(" + rolls + ") " + ModUtil.FORMAT.format(value) + unit;
    }
    return text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModStat other = (ModStat) o;
    return (unit == other.unit) && (rolls == other.rolls) && (Math.abs(value - other.value) < TOLERANCE);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rolls, value, unit);
  }

}
