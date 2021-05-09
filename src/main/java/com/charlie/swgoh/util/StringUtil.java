package com.charlie.swgoh.util;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class StringUtil {

  public static final int MOD_STAT_MATCH_THRESHOLD = 85;

  private StringUtil() {}

  public static String stripSpaces(String s) {
    return s.replace(" ", "");
  }

  public static String prepareForMatching(String s) {
    return s.replace('0', 'O');
  }

  public static boolean fuzzyMatch(String s1, String s2) {
    return FuzzySearch.ratio(s1, s2) >= MOD_STAT_MATCH_THRESHOLD;
  }

}
