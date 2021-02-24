package com.charlie.swgoh.datamodel.json;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

public class Mod {

  private String primaryBonusType;
  private String primaryBonusValue;
  private String secondaryType1;
  private String secondaryValue1;
  private int secondaryRoll1;
  private String secondaryType2;
  private String secondaryValue2;
  private int secondaryRoll2;
  private String secondaryType3;
  private String secondaryValue3;
  private int secondaryRoll3;
  private String secondaryType4;
  private String secondaryValue4;
  private int secondaryRoll4;
  private String uid;
  private String slot;
  private String set;
  private int level;
  private int pips;
  private String characterID;
  private int tier;

  @JsonGetter
  public String getPrimaryBonusType() {
    return primaryBonusType;
  }

  @JsonSetter
  public void setPrimaryBonusType(String primaryBonusType) {
    this.primaryBonusType = primaryBonusType;
  }

  @JsonGetter
  public String getPrimaryBonusValue() {
    return primaryBonusValue;
  }

  @JsonSetter
  public void setPrimaryBonusValue(String primaryBonusValue) {
    this.primaryBonusValue = primaryBonusValue;
  }

  @JsonGetter("secondaryType_1")
  public String getSecondaryType1() {
    return secondaryType1;
  }

  @JsonSetter("secondaryType_1")
  public void setSecondaryType1(String secondaryType1) {
    this.secondaryType1 = secondaryType1;
  }

  @JsonGetter("secondaryValue_1")
  public String getSecondaryValue1() {
    return secondaryValue1;
  }

  @JsonSetter("secondaryValue_1")
  public void setSecondaryValue1(String secondaryValue1) {
    this.secondaryValue1 = secondaryValue1;
  }

  @JsonGetter("secondaryRoll_1")
  public int getSecondaryRoll1() {
    return secondaryRoll1;
  }

  @JsonSetter("secondaryRoll_1")
  public void setSecondaryRoll1(int secondaryRoll1) {
    this.secondaryRoll1 = secondaryRoll1;
  }

  @JsonGetter("secondaryType_2")
  public String getSecondaryType2() {
    return secondaryType2;
  }

  @JsonSetter("secondaryType_2")
  public void setSecondaryType2(String secondaryType2) {
    this.secondaryType2 = secondaryType2;
  }

  @JsonGetter("secondaryValue_2")
  public String getSecondaryValue2() {
    return secondaryValue2;
  }

  @JsonSetter("secondaryValue_2")
  public void setSecondaryValue2(String secondaryValue2) {
    this.secondaryValue2 = secondaryValue2;
  }

  @JsonGetter("secondaryRoll_2")
  public int getSecondaryRoll2() {
    return secondaryRoll2;
  }

  @JsonSetter("secondaryRoll_2")
  public void setSecondaryRoll2(int secondaryRoll2) {
    this.secondaryRoll2 = secondaryRoll2;
  }

  @JsonGetter("secondaryType_3")
  public String getSecondaryType3() {
    return secondaryType3;
  }

  @JsonSetter("secondaryType_3")
  public void setSecondaryType3(String secondaryType3) {
    this.secondaryType3 = secondaryType3;
  }

  @JsonGetter("secondaryValue_3")
  public String getSecondaryValue3() {
    return secondaryValue3;
  }

  @JsonSetter("secondaryValue_3")
  public void setSecondaryValue3(String secondaryValue3) {
    this.secondaryValue3 = secondaryValue3;
  }

  @JsonGetter("secondaryRoll_3")
  public int getSecondaryRoll3() {
    return secondaryRoll3;
  }

  @JsonSetter("secondaryRoll_3")
  public void setSecondaryRoll3(int secondaryRoll3) {
    this.secondaryRoll3 = secondaryRoll3;
  }

  @JsonGetter("secondaryType_4")
  public String getSecondaryType4() {
    return secondaryType4;
  }

  @JsonSetter("secondaryType_4")
  public void setSecondaryType4(String secondaryType4) {
    this.secondaryType4 = secondaryType4;
  }

  @JsonGetter("secondaryValue_4")
  public String getSecondaryValue4() {
    return secondaryValue4;
  }

  @JsonSetter("secondaryValue_4")
  public void setSecondaryValue4(String secondaryValue4) {
    this.secondaryValue4 = secondaryValue4;
  }

  @JsonGetter("secondaryRoll_4")
  public int getSecondaryRoll4() {
    return secondaryRoll4;
  }

  @JsonSetter("secondaryRoll_4")
  public void setSecondaryRoll4(int secondaryRoll4) {
    this.secondaryRoll4 = secondaryRoll4;
  }

  @JsonGetter("mod_uid")
  public String getUid() {
    return uid;
  }

  @JsonSetter("mod_uid")
  public void setUid(String uid) {
    this.uid = uid;
  }

  @JsonGetter
  public String getSlot() {
    return slot;
  }

  @JsonSetter
  public void setSlot(String slot) {
    this.slot = slot;
  }

  @JsonGetter
  public String getSet() {
    return set;
  }

  @JsonSetter
  public void setSet(String set) {
    this.set = set;
  }

  @JsonGetter
  public int getLevel() {
    return level;
  }

  @JsonSetter
  public void setLevel(int level) {
    this.level = level;
  }

  @JsonGetter
  public int getPips() {
    return pips;
  }

  @JsonSetter
  public void setPips(int pips) {
    this.pips = pips;
  }

  @JsonGetter
  public String getCharacterID() {
    return characterID;
  }

  @JsonSetter
  public void setCharacterID(String characterID) {
    this.characterID = characterID;
  }

  @JsonGetter
  public int getTier() {
    return tier;
  }

  @JsonSetter
  public void setTier(int tier) {
    this.tier = tier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Mod mod = (Mod) o;
    return getSecondaryRoll1() == mod.getSecondaryRoll1() && getSecondaryRoll2() == mod.getSecondaryRoll2()
            && getSecondaryRoll3() == mod.getSecondaryRoll3() && getSecondaryRoll4() == mod.getSecondaryRoll4()
            && getLevel() == mod.getLevel() && getPips() == mod.getPips() && getTier() == mod.getTier()
            && Objects.equals(getPrimaryBonusType(), mod.getPrimaryBonusType())
            && Objects.equals(getPrimaryBonusValue(), mod.getPrimaryBonusValue())
            && Objects.equals(getSecondaryType1(), mod.getSecondaryType1())
            && Objects.equals(getSecondaryValue1(), mod.getSecondaryValue1())
            && Objects.equals(getSecondaryType2(), mod.getSecondaryType2())
            && Objects.equals(getSecondaryValue2(), mod.getSecondaryValue2())
            && Objects.equals(getSecondaryType3(), mod.getSecondaryType3())
            && Objects.equals(getSecondaryValue3(), mod.getSecondaryValue3())
            && Objects.equals(getSecondaryType4(), mod.getSecondaryType4())
            && Objects.equals(getSecondaryValue4(), mod.getSecondaryValue4())
            && Objects.equals(getUid(), mod.getUid()) && Objects.equals(getSlot(), mod.getSlot()) && Objects.equals(getSet(), mod.getSet())
            && Objects.equals(getCharacterID(), mod.getCharacterID());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
            getPrimaryBonusType(), getPrimaryBonusValue(), getSecondaryType1(), getSecondaryValue1(),
            getSecondaryRoll1(), getSecondaryType2(), getSecondaryValue2(), getSecondaryRoll2(),
            getSecondaryType3(), getSecondaryValue3(), getSecondaryRoll3(), getSecondaryType4(),
            getSecondaryValue4(), getSecondaryRoll4(), getUid(), getSlot(), getSet(), getLevel(),
            getPips(), getCharacterID(), getTier()
    );
  }

}
