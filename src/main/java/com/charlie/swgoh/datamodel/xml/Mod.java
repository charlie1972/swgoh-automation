package com.charlie.swgoh.datamodel.xml;

import com.charlie.swgoh.connector.ModStatXmlAdapter;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.ModStat;
import com.charlie.swgoh.datamodel.ModTier;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Mod {

  @XmlElement(name = "fromCharacter")
  private String fromCharacter;

  @XmlElement(name = "character")
  private String character;

  @XmlElement(name = "dots")
  private int dots;

  @XmlElement(name = "level")
  private int level;

  @XmlElement(name = "slot")
  private ModSlot slot;

  @XmlElement(name = "tier")
  private ModTier tier;

  @XmlElement(name = "set")
  private ModSet set;

  @XmlElement(name = "primary-stat")
  @XmlJavaTypeAdapter(ModStatXmlAdapter.class)
  private ModStat primaryStat;

  @XmlElement(name = "secondary-stat")
  @XmlJavaTypeAdapter(ModStatXmlAdapter.class)
  private List<ModStat> secondaryStats;

  public String getFromCharacter() {
    return fromCharacter;
  }

  public String getCharacter() {
    return character;
  }

  public int getDots() {
    return dots;
  }

  public int getLevel() {
    return level;
  }

  public ModSlot getSlot() {
    return slot;
  }

  public ModTier getTier() {
    return tier;
  }

  public ModSet getSet() {
    return set;
  }

  public ModStat getPrimaryStat() {
    return primaryStat;
  }

  public List<ModStat> getSecondaryStats() {
    return secondaryStats;
  }

  public void setFromCharacter(String fromCharacter) {
    this.fromCharacter = fromCharacter;
  }

  public void setCharacter(String character) {
    this.character = character;
  }

  public void setDots(int dots) {
    this.dots = dots;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setSlot(ModSlot slot) {
    this.slot = slot;
  }

  public void setTier(ModTier tier) {
    this.tier = tier;
  }

  public void setSet(ModSet set) {
    this.set = set;
  }

  public void setPrimaryStat(ModStat primaryStat) {
    this.primaryStat = primaryStat;
  }

  public void setSecondaryStats(List<ModStat> secondaryStats) {
    this.secondaryStats = secondaryStats;
  }

  @Override
  public String toString() {
    return "Mod{" +
            "fromCharacter='" + fromCharacter + '\'' +
            ", character='" + character + '\'' +
            ", dots=" + dots +
            ", level=" + level +
            ", slot=" + slot +
            ", tier=" + tier +
            ", set=" + set +
            ", primaryStat=" + primaryStat +
            ", secondaryStats=" + secondaryStats +
            '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Mod)) {
      return false;
    }
    Mod other = (Mod)obj;
    return this.primaryStat.equals(other.primaryStat) && this.secondaryStats.equals(other.secondaryStats);
  }

}
