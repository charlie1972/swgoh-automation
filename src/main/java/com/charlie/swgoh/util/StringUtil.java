package com.charlie.swgoh.util;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.regex.Pattern;

public class StringUtil {

  public static final Pattern INVALID_CHARACTERS = Pattern.compile("[^0-9A-Za-z+.%()]");
  public static final int MOD_STAT_MATCH_THRESHOLD = 85;

  private StringUtil() {}

  public static String stripSpaces(String s) {
    return s.replace(" ", "");
  }

  public static String prepareModStatForParsing(String s) {
    String temp = s.replace('{', '(').replace('}', ')');
    return INVALID_CHARACTERS.matcher(temp).replaceAll("");
  }

  public static String prepareForMatching(String s) {
    return s.replace('0', 'O');
  }

  public static boolean fuzzyMatch(String s1, String s2) {
    return FuzzySearch.ratio(s1.toUpperCase(), s2.toUpperCase()) >= MOD_STAT_MATCH_THRESHOLD;
  }

}
