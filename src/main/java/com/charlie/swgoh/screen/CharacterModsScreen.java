package com.charlie.swgoh.screen;

import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharacterModsScreen {

  private CharacterModsScreen() {}

  private static final Logger LOG = LoggerFactory.getLogger(CharacterModsScreen.class);

  private static final Pattern TITLE_TEXT = new Pattern("character_mods_screen_title.png");
  private static final Pattern FILTER_TITLE_TEXT = new Pattern("character_mods_screen_filter_title.png");
  private static final Pattern FILTER_TITLE_OK = new Pattern("character_mods_screen_filter_ok.png");

  private static final Map<String, String> nameSubstitution = new HashMap<>();
  static {
    nameSubstitution.put("Chirrut Îmwe", "Chirrut");
    nameSubstitution.put("Padmé Amidala", "Amidala");
  }

  private static Location locFilterButton;
  private static Location locFilterAllCheckbox;
  private static Location locFilterTextZone;
  private static List<Region> regCharacterNames;
  private static Region regTitle;
  private static Region regFilterTitle;
  private static Region regFilterOK;

  public static boolean waitForCharacterModsTitle() {
    return AutomationUtil.waitForPattern(getRegTitle(), TITLE_TEXT, "Waiting for title text");
  }

  public static boolean waitForCharacterModsFilterTitle() {
    return AutomationUtil.waitForPattern(getRegFilterTitle(), FILTER_TITLE_TEXT, "Waiting for filter title text");
  }

  public static boolean waitForCharacterModsFilterOK() {
    return AutomationUtil.waitForPattern(getRegFilterOK(), FILTER_TITLE_OK, "Waiting for filter OK");
  }

  public static void filterName(String name) {
    String nameToFilter = nameSubstitution.getOrDefault(name, name);

    AutomationUtil.click(getLocFilterButton(), "Clicking on filter button");
    if (!waitForCharacterModsFilterTitle()) {
      LOG.error("Character mod screen filter: title text not found. Aborting.");
      throw new ProcessException("Character mod screen filter: title text not found. Aborting.");
    }
    AutomationUtil.click(getLocFilterAllCheckbox(), "Clicking on ALL checkbox");
    AutomationUtil.click(getLocFilterTextZone(), "Clicking on filter text zone");
    if (!waitForCharacterModsFilterOK()) {
      LOG.error("Character mod screen filter: OK button not found. Aborting.");
      throw new ProcessException("Character mod screen filter: OK button not found. Aborting.");
    }
    AutomationUtil.typeText(nameToFilter, "Pasting character name in filter");
    AutomationUtil.waitFor(250L);
    AutomationUtil.typeText("\n", "Typing ENTER in filter");
    if (!waitForCharacterModsTitle()) {
      LOG.error("Character mod screen: title text not found. Aborting.");
      throw new ProcessException("Character mod screen: title text not found. Aborting.");
    }
  }

  public static String readCharacterName(int position) {
    List<String> strings = AutomationUtil.readLines(getRegCharacterNames().get(position));
    return String.join(" ", strings);
  }

  public static void enterModScreen(String name) {
    LOG.info("Scanning for name {}", name);
    int maxScore = -1;
    int position = -1;
    for (int i = 0; i < 6; i++) {
      String readName = CharacterModsScreen.readCharacterName(i);
      LOG.info("Read name at position {}: {}", i, readName);
      if (readName.trim().isEmpty()) {
        LOG.info("Nothing read. Stopping.");
        break;
      }
      int score = FuzzySearch.ratio(name, readName);
      LOG.info("Matching score: {}", score);
      if (score > 85) {
        position = i;
        break;
      }
      if (score > maxScore) {
        maxScore = score;
        position = i;
      }
    }
    if (position >= 0) {
      LOG.info("Found character {} at position {}", name, position);
      AutomationUtil.click(getRegCharacterNames().get(position).getCenter(), "Clicking on character at position " + position);
    }
    else {
      throw new ProcessException("Character " + name + " not found. Aborting.");
    }
  }

  public static void init() {
    locFilterButton = AutomationUtil.getLocation(320, 150);
    locFilterAllCheckbox = AutomationUtil.getLocation(470, 225);
    locFilterTextZone = AutomationUtil.getLocation(640, 150);

    regCharacterNames = IntStream.range(0, 6)
            .mapToObj(position -> AutomationUtil.getRegion(151 + position * 190, 212, 96, 59))
            .collect(Collectors.toList());
    regTitle = AutomationUtil.getRegion(98, 24, 194, 35);
    regFilterTitle = AutomationUtil.getRegion(552, 56, 177, 38);
    regFilterOK = AutomationUtil.getRegion(1174, 649, 48, 37);
  }

  public static Location getLocFilterButton() {
    return locFilterButton;
  }

  public static Location getLocFilterAllCheckbox() {
    return locFilterAllCheckbox;
  }

  public static Location getLocFilterTextZone() {
    return locFilterTextZone;
  }

  public static List<Region> getRegCharacterNames() {
    return regCharacterNames;
  }

  public static Region getRegTitle() {
    return regTitle;
  }

  public static Region getRegFilterTitle() {
    return regFilterTitle;
  }

  public static Region getRegFilterOK() {
    return regFilterOK;
  }

}
