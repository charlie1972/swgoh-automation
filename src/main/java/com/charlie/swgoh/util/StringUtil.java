package com.charlie.swgoh.util;

public class StringUtil {

  private StringUtil() {}

  public static String stripSpaces(String s) {
    return s.replace(" ", "");
  }

  public static String prepareForMatching(String s) {
    return s.replace('0', 'O');
  }

}
