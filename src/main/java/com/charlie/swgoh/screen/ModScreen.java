package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModScreen {

  private ModScreen() {}

  public enum StateAfterModMoveOrder {
    NONE,
    REMOVE_BUTTON,
    ASSIGN_LOADOUT_BUTTON,
    FILTER_AND_SORT_BUTTONS
  }

  public static class DotsTierSetAndSlot {
    private final int dots;
    private final ModTier tier;
    private final ModSet set;
    private final ModSlot slot;

    public DotsTierSetAndSlot(int dots, ModTier tier, ModSet set, ModSlot slot) {
      this.dots = dots;
      this.tier = tier;
      this.set = set;
      this.slot = slot;
    }

    public int getDots() {
      return dots;
    }

    public ModTier getTier() {
      return tier;
    }

    public ModSet getSet() {
      return set;
    }

    public ModSlot getSlot() {
      return slot;
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(ModScreen.class);

  private static final java.util.regex.Pattern REGEX_MOD_DESCRIPTION = java.util.regex.Pattern.compile("^MK\\s+([IV]{1,3})-([A-E])\\s+([A-Z ]+)\\s+([A-Z\\-]+?)$");

  private static final Map<String, Integer> ROMAN_NUMERALS_TO_INTEGER_MAP = new HashMap<>();
  static {
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("I", 1);
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("II", 2);
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("III", 3);
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("IV", 4);
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("V", 5);
    ROMAN_NUMERALS_TO_INTEGER_MAP.put("VI", 6);
  }

  private static final Pattern FILTER_AND_SORT_BUTTONS = new Pattern("mod_screen_filter_and_sort_buttons.png");
  private static final Pattern MINUS_BUTTON = new Pattern("mod_screen_minus_button.png");
  private static final Pattern UNASSIGNED_LABEL = new Pattern("mod_screen_unassigned.png");
  private static final Pattern MOD_DOT = new Pattern("mod_dot.png");
  private static final Pattern ASSIGN_MOD_REMOVE_BUTTON = new Pattern("mod_screen_assign_mod_remove.png");
  private static final Pattern ASSIGN_LOADOUT_ASSIGN_BUTTON = new Pattern("mod_screen_assign_loadout_assign.png");
  private static final Pattern REVERT_BUTTON = new Pattern("mod_screen_revert.png");
  private static final Pattern SCROLL_BAR_SINGLE_LINE = new Pattern("mod_screen_single_line_scroll.png");

  private static Location locBackButton;
  private static Location locFilterButton;
  private static Map<ModSlot, Location> locCharModMap;
  private static List<Location> locOtherMods;
  private static Location locConfirmButton;
  private static Region regCharacterName;
  private static Region regCharacterModPrimaryStat;
  private static List<Region> regCharacterModSecondaryStats;
  private static Region regOtherModPrimaryStat;
  private static List<Region> regOtherModSecondaryStats;
  private static Region regFilterAndSortButtons;
  private static List<Region> regModDots;
  private static Region regBelowFirstModDot;
  private static Region regMinusButton;
  private static Region regUnassignedLabel;
  private static Region regRemoveButton;
  private static Region regAssignLoadoutButton;
  private static Region regRevertButton;
  private static Region regOtherModDots;
  private static Region regOtherModSetAndSlot;
  private static Region regOtherModLevelAndTier;
  private static Region regModScrollbar;

  public static boolean waitForFilterAndSortButtons() {
    return AutomationUtil.waitForPattern(getRegFilterAndSortButtons(), FILTER_AND_SORT_BUTTONS, "Waiting for filter and sort buttons");
  }

  public static boolean waitForMinusButton() {
    return AutomationUtil.waitForPattern(getRegMinusButton(), MINUS_BUTTON, "Waiting for minus button");
  }

  public static boolean checkName(String name) {
    String readName = AutomationUtil.readLine(getRegCharacterName());
    int score = FuzzySearch.ratio(prepareForMatching(name), prepareForMatching(readName));
    boolean isPassed = score > 85;
    LOG.info("Checked name: {}. Read on screen: {}. Score: {}. Passed: {}", name, readName, score, isPassed);
    return isPassed;
  }

  private static String prepareForMatching(String s) {
    return s.replace('0', 'O');
  }

  public static boolean checkForMinusButton() {
    return AutomationUtil.checkForPattern(getRegMinusButton(), MINUS_BUTTON, "Waiting for minus button");
  }

  public static boolean checkForUnassignedLabel() {
    return AutomationUtil.checkForPattern(getRegUnassignedLabel(), UNASSIGNED_LABEL, "Checking for unassigned label");
  }

  public static boolean checkForRevertButton() {
    return AutomationUtil.waitForPattern(getRegRevertButton(), REVERT_BUTTON, "Checking for revert button");
  }

  public static StateAfterModMoveOrder waitAndGetStateAfterModMoveOrder() {
    LOG.info("Checking state after mod move order");
    long startTimeMillis = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTimeMillis < 5000L) {
      if (AutomationUtil.checkForPattern(getRegRemoveButton(), ASSIGN_MOD_REMOVE_BUTTON, "Checking for mod remove button")) {
        LOG.info("State: REMOVE_BUTTON");
        return StateAfterModMoveOrder.REMOVE_BUTTON;
      }
      if (AutomationUtil.checkForPattern(getRegAssignLoadoutButton(), ASSIGN_LOADOUT_ASSIGN_BUTTON, "Checking for loadout assign button")) {
        LOG.info("State: ASSIGN_LOADOUT_BUTTON");
        return StateAfterModMoveOrder.ASSIGN_LOADOUT_BUTTON;
      }
      if (AutomationUtil.checkForPattern(getRegFilterAndSortButtons(), FILTER_AND_SORT_BUTTONS, "Checking for filter and sort buttons")) {
        LOG.info("State: FILTER_AND_SORT_BUTTONS");
        return StateAfterModMoveOrder.FILTER_AND_SORT_BUTTONS;
      }
      AutomationUtil.waitFor(200L);
    }
    LOG.info("Timeout. State: NONE");
    return StateAfterModMoveOrder.NONE;
  }

  public static void enterModFilter() {
    AutomationUtil.click(getLocFilterButton(), "Clicking on filter button");
  }

  public static void exitModScreen() {
    AutomationUtil.click(getLocBackButton(), "Clicking on back button");
  }

  public static void dragOtherModsToTop() {
    try {
      BlueStacksApp.getWindow().dragDrop(
              getLocOtherMods().get(0),
              getLocOtherMods().get(8)
      );
    }
    catch (FindFailed ffe) {
      throw new ProcessException("Could not drag the other mods to the top");
    }
  }

  public static ModWithStatsInText extractModText(boolean isCharacter) {
    Region primaryStatRegion = isCharacter ? ModScreen.getRegCharacterModPrimaryStat() : ModScreen.getRegOtherModPrimaryStat();
    List<Region> secondaryStatRegions = isCharacter ? ModScreen.getRegCharacterModSecondaryStats() : ModScreen.getRegOtherModSecondaryStats();

    String primaryStatString = AutomationUtil.readLine(primaryStatRegion);
    List<String> secondaryStatStrings = secondaryStatRegions.stream()
            .map(AutomationUtil::readLine)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    LOG.info("Extracted mod texts: {} / {}", primaryStatString, secondaryStatStrings);
    return new ModWithStatsInText(primaryStatString, secondaryStatStrings);
  }

  public static Mod extractModStats(boolean isCharacter) {
    ModWithStatsInText modWithStatsInText = extractModText(isCharacter);

    Mod mod = new Mod();
    ModStat primaryStat = new ModStat(modWithStatsInText.getPrimaryStat());
    List<ModStat> secondaryStats = modWithStatsInText.getSecondaryStats().stream()
            .map(ModStat::new)
            .collect(Collectors.toList());
    mod.setPrimaryStat(primaryStat);
    mod.setSecondaryStats(secondaryStats);

    return mod;
  }

  public static int countModsFromDots() {
    for (int i = 0; i < getRegModDots().size(); i++) {
      if (!getRegModDots().get(i).has(MOD_DOT)) {
        return i;
      }
    }
    return getRegModDots().size();
  }

  public static DotsTierSetAndSlot extractOtherModDotsTierSetAndSlot() {
    String text = AutomationUtil.readLine(getRegOtherModSetAndSlot());
    Matcher matcher = REGEX_MOD_DESCRIPTION.matcher(text);
    if (!matcher.matches()) {
      LOG.warn("Unable to parse tier, set and slot from: {}", text);
      return null;
    }
    String dotsAsString = matcher.group(1);
    Integer dots = ROMAN_NUMERALS_TO_INTEGER_MAP.get(dotsAsString);
    String tierAsString = matcher.group(2);
    ModTier tier = ModTier.valueOf(tierAsString);
    String setAsString = matcher.group(3);
    ModSet set = ModSet.fromString(setAsString);
    String slotAsString = matcher.group(4);
    ModSlot slot = ModSlot.fromName(slotAsString);
    if (Stream.of(dots, tier, set, slot).anyMatch(Objects::isNull)) {
      return null;
    }
    return new DotsTierSetAndSlot(dots, tier, set, slot);
  }

  public static int extractOtherModLevel() {
    String text = AutomationUtil.readLine(getRegOtherModLevelAndTier());
    int pos = text.indexOf("-");
    if (pos >= 0) {
      String levelAsString = text.substring(0, pos).trim();
      try {
        return Integer.parseInt(levelAsString);
      }
      catch (NumberFormatException e) {
        throw new ProcessException("Could not extract the mod level from: " + text);      }
    }
    else {
      throw new ProcessException("Could not extract the mod level from: " + text);
    }
  }

  public static boolean dragOtherModsListOneLineUp() {
    AutomationUtil.mouseMove(ModScreen.getLocOtherMods().get(12), "Move mouse to 1st mod of 4th line");
    BlueStacksApp.getWindow().mouseDown(Button.LEFT);
    int mouseY = ModScreen.getLocOtherMods().get(12).getY();
    boolean isOK = false;
    while (mouseY > ModScreen.getLocOtherMods().get(0).getY()) {
      BlueStacksApp.getWindow().mouseMove(0, -11);
      mouseY -= 11;
      if (AutomationUtil.checkForPattern(ModScreen.getRegBelowFirstModDot(), MOD_DOT, "Check if dot is just below for top left mod")) {
        isOK = true;
        break;
      }
    }
    if (!isOK) {
      throw new ProcessException("Could not scroll up the mod list");
    }
    isOK = false;
    while (mouseY > ModScreen.getLocOtherMods().get(0).getY()) {
      BlueStacksApp.getWindow().mouseMove(0, -5);
      mouseY -= 5;
      if (AutomationUtil.checkForPattern(ModScreen.getRegModDots().get(0), MOD_DOT, "Check if dot is correctly placed for top left mod")) {
        isOK = true;
        break;
      }
    }
    if (!isOK) {
      throw new ProcessException("Could not scroll up the mod list");
    }
    BlueStacksApp.getWindow().mouseUp();
    AutomationUtil.waitFor(750L);

    return AutomationUtil.checkForPattern(ModScreen.getRegModDots().get(0), MOD_DOT, "Check if drag has been correctly performed");
  }

  public static double computeModProgress() {
    List<Match> matches = getRegModScrollbar().findAllList(SCROLL_BAR_SINGLE_LINE).stream().sorted(Comparator.comparing(Match::getY)).collect(Collectors.toList());
    if (matches.size() == 1) {
      return 1d;
    }
    int topHighlightY = matches.get(0).getY();
    int bottomHightlightY = matches.get(matches.size() - 1).getY();
    int highlightedScrollHeight = bottomHightlightY - topHighlightY;
    int effectiveScrollbarHeight = getRegModScrollbar().getH() - highlightedScrollHeight;
    int effectiveHighlightPos = topHighlightY - getRegModScrollbar().getY();
    return (double)effectiveHighlightPos / (double)effectiveScrollbarHeight;
  }

  // Init

  public static void init() {
    locBackButton = AutomationUtil.getLocation(44, 44);
    locFilterButton = AutomationUtil.getLocation(165, 141);
    locCharModMap = new LinkedHashMap<>();
    locCharModMap.put(ModSlot.SQUARE, AutomationUtil.getLocation(587, 185));
    locCharModMap.put(ModSlot.ARROW, AutomationUtil.getLocation(700, 165));
    locCharModMap.put(ModSlot.DIAMOND, AutomationUtil.getLocation(600, 370));
    locCharModMap.put(ModSlot.TRIANGLE, AutomationUtil.getLocation(686, 335));
    locCharModMap.put(ModSlot.CIRCLE, AutomationUtil.getLocation(600, 510));
    locCharModMap.put(ModSlot.CROSS, AutomationUtil.getLocation(700, 465));
    locOtherMods = new ArrayList<>(16);
    for (int iy = 0; iy < 4; iy++) {
      for (int ix = 0; ix < 4; ix++) {
        locOtherMods.add(
                AutomationUtil.getLocation(116 + ix * 100, 235 + iy * 120)
        );
      }
    }
    locConfirmButton = AutomationUtil.getLocation(1120, 586);

    regCharacterName = AutomationUtil.getRegion(172, 18, 500, 30);
    regCharacterModPrimaryStat = AutomationUtil.getRegion(991, 171, 200, 25);
    regCharacterModSecondaryStats = Arrays.asList(
            AutomationUtil.getRegion(991, 224, 200, 25),
            AutomationUtil.getRegion(991, 247, 200, 25),
            AutomationUtil.getRegion(991, 271, 200, 25),
            AutomationUtil.getRegion(991, 295, 200, 25)
    );
    regOtherModPrimaryStat = AutomationUtil.getRegion(991, 478, 200, 25);
    regOtherModSecondaryStats = Arrays.asList(
            AutomationUtil.getRegion(991, 531, 200, 25),
            AutomationUtil.getRegion(991, 554, 200, 25),
            AutomationUtil.getRegion(991, 578, 200, 25),
            AutomationUtil.getRegion(991, 602, 200, 25)
    );
    regFilterAndSortButtons = AutomationUtil.getRegion(50, 104, 443, 75);
    regModDots = new ArrayList<>(16);
    for (int iy = 0; iy < 4; iy++) {
      for (int ix = 0; ix < 4; ix++) {
        regModDots.add(
                AutomationUtil.getRegion(76 + ix * 99, 180 + iy * 119, 13, 25)
        );
      }
    }
    regBelowFirstModDot = AutomationUtil.getRegion(76, 205, 13, 22);
    regMinusButton = AutomationUtil.getRegion(1161, 92, 85, 84);
    regUnassignedLabel = AutomationUtil.getRegion(793, 110, 155, 30);
    regRemoveButton = AutomationUtil.getRegion(673, 452, 119, 42);
    regAssignLoadoutButton = AutomationUtil.getRegion(659, 554, 94, 38);
    regRevertButton = AutomationUtil.getRegion(854, 568, 100, 36);
    regOtherModDots = AutomationUtil.getRegion(852, 458, 84, 12);
    regOtherModSetAndSlot = AutomationUtil.getRegion(793, 420, 424, 26);
    regOtherModLevelAndTier = AutomationUtil.getRegion(870, 528, 48, 18);
    regModScrollbar = AutomationUtil.getRegion(470, 182, 4, 432);
  }

  public static Location getLocBackButton() {
    return locBackButton;
  }

  public static Location getLocFilterButton() {
    return locFilterButton;
  }

  public static Map<ModSlot, Location> getLocCharModMap() {
    return locCharModMap;
  }

  public static List<Location> getLocOtherMods() {
    return locOtherMods;
  }

  public static Location getLocConfirmButton() {
    return locConfirmButton;
  }

  public static Region getRegCharacterName() {
    return regCharacterName;
  }

  public static Region getRegCharacterModPrimaryStat() {
    return regCharacterModPrimaryStat;
  }

  public static List<Region> getRegCharacterModSecondaryStats() {
    return regCharacterModSecondaryStats;
  }

  public static Region getRegOtherModPrimaryStat() {
    return regOtherModPrimaryStat;
  }

  public static List<Region> getRegOtherModSecondaryStats() {
    return regOtherModSecondaryStats;
  }

  public static Region getRegFilterAndSortButtons() {
    return regFilterAndSortButtons;
  }

  public static Region getRegBelowFirstModDot() {
    return regBelowFirstModDot;
  }

  public static List<Region> getRegModDots() {
    return regModDots;
  }

  public static Region getRegMinusButton() {
    return regMinusButton;
  }

  public static Region getRegUnassignedLabel() {
    return regUnassignedLabel;
  }

  public static Region getRegRemoveButton() {
    return regRemoveButton;
  }

  public static Region getRegAssignLoadoutButton() {
    return regAssignLoadoutButton;
  }

  public static Region getRegRevertButton() {
    return regRevertButton;
  }

  public static Region getRegOtherModDots() {
    return regOtherModDots;
  }

  public static Region getRegOtherModSetAndSlot() {
    return regOtherModSetAndSlot;
  }

  public static Region getRegOtherModLevelAndTier() {
    return regOtherModLevelAndTier;
  }

  public static Region getRegModScrollbar() {
    return regModScrollbar;
  }

}
