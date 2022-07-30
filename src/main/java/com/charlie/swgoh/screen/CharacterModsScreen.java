package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharacterModsScreen {

  private CharacterModsScreen() {}

  private static final Logger LOG = LoggerFactory.getLogger(CharacterModsScreen.class);

  // Image patterns
  static {
    Configuration.configureImagePath();
  }
  private static final Pattern P_CHARACTERS_TAB = new Pattern("character_mods_screen_characters_tab.png");
  private static final Pattern P_MODS_CHECKED = new Pattern("character_mods_screen_mods_checked.png");
  private static final Pattern P_FILTER_TITLE_TEXT = new Pattern("character_mods_screen_filter_title.png");
  private static final Pattern P_FILTER_WHITE_SQUARE = new Pattern("character_mods_screen_filter_white_square.png");

  private static final List<String> NAME_PARTS = Arrays.asList(
          "Chirrut",
          "Amidala"
  );

  private static final int MATCH_THRESHOLD = 90;

  // Locations
  public static final Location L_FILTER_BUTTON = new Location(380, 130);
  public static final Location L_FILTER_ALL_CHECKBOX = new Location(470, 225);
  public static final Location L_FILTER_TEXT_ZONE = new Location(640, 150);

  // Regions
  public static final List<Region> RL_CHARACTER_NAMES = IntStream.range(0, 5)
          .mapToObj(position -> new Region(362 + position * 190, 191, 89, 53))
          .collect(Collectors.toList());
  public static final Region R_CHARACTERS = new Region(22, 122, 155, 41);
  public static final Region R_MODS_CHECKED = new Region(835, 107, 50, 50);
  public static final Region R_FILTER_TITLE = new Region(552, 56, 177, 38);
  public static final Region R_FILTER_WHITE_SQUARE = new Region(5, 715, 5, 5);

  public static boolean waitForCharactersTab() {
    return AutomationUtil.waitForPattern(R_CHARACTERS, P_CHARACTERS_TAB, "Waiting for characters tab");
  }

  public static boolean checkModsCheckbox() {
    return AutomationUtil.checkForPattern(R_MODS_CHECKED, P_MODS_CHECKED, "Checking the mods checkbox");
  }

  public static boolean waitForCharacterModsFilterTitle() {
    return AutomationUtil.waitForPattern(R_FILTER_TITLE, P_FILTER_TITLE_TEXT, "Waiting for filter title text");
  }

  public static boolean waitForCharacterModsFilterWhiteSquare() {
    return AutomationUtil.waitForPattern(R_FILTER_WHITE_SQUARE, P_FILTER_WHITE_SQUARE, "Waiting for white square");
  }

  public static void filterName(String name) {
    String nameToFilter = name;
    for (String n : NAME_PARTS) {
      if (name.contains(n)) {
        nameToFilter = n;
        break;
      }
    }

    AutomationUtil.click(L_FILTER_BUTTON, "Clicking on filter button");
    if (!waitForCharacterModsFilterTitle()) {
      LOG.error("Character mod screen filter: title text not found. Aborting.");
      throw new ProcessException("Character mod screen filter: title text not found. Aborting.");
    }
    AutomationUtil.click(L_FILTER_ALL_CHECKBOX, "Clicking on ALL checkbox");
    AutomationUtil.click(L_FILTER_TEXT_ZONE, "Clicking on filter text zone");
    if (!waitForCharacterModsFilterWhiteSquare()) {
      LOG.error("Character mod screen filter: white square not found. Aborting.");
      throw new ProcessException("Character mod screen filter: white square not found. Aborting.");
    }
    AutomationUtil.typeText(nameToFilter, "Pasting character name in filter");
    AutomationUtil.waitFor(250L);
    AutomationUtil.typeText("\n", "Typing ENTER in filter");
    if (!waitForCharactersTab()) {
      LOG.error("Character mods screen: characters tab not found. Aborting.");
      throw new ProcessException("Character mods screen: characters tab not found. Aborting.");
    }
  }

  public static String readCharacterName(int position) {
    List<String> strings = AutomationUtil.readLines(RL_CHARACTER_NAMES.get(position));
    return String.join(" ", strings);
  }

  public static void enterModScreen(String name) {
    LOG.info("Scanning for name {}", name);
    int maxScore = -1;
    int position = -1;
    for (int i = 0; i < RL_CHARACTER_NAMES.size(); i++) {
      String readName = CharacterModsScreen.readCharacterName(i);
      LOG.info("Read name at position {}: {}", i, readName);
      if (readName.trim().isEmpty()) {
        LOG.info("Nothing read. Stopping.");
        break;
      }
      int score = FuzzySearch.ratio(name, readName);
      LOG.info("Matching score: {}", score);
      if (score >= MATCH_THRESHOLD) {
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
      AutomationUtil.click(RL_CHARACTER_NAMES.get(position).getCenter(), "Clicking on character at position " + position);
    }
    else {
      throw new ProcessException("Character " + name + " not found. Aborting.");
    }
  }

}
