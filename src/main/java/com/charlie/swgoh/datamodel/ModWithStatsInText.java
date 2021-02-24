package com.charlie.swgoh.datamodel;

import java.util.List;

public class ModWithStatsInText {

  private final String primaryStat;
  private final List<String> secondaryStats;

  public ModWithStatsInText(String primaryStat, List<String> secondaryStats) {
    this.primaryStat = primaryStat;
    this.secondaryStats = secondaryStats;
  }

  public String getPrimaryStat() {
    return primaryStat;
  }

  public List<String> getSecondaryStats() {
    return secondaryStats;
  }

  @Override
  public String toString() {
    return "ModWithStatsInText{" +
            "primaryStat='" + primaryStat + '\'' +
            ", secondaryStats=" + secondaryStats +
            '}';
  }

}
