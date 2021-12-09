package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.StringUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
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

  private static class ModLocationIterator implements Iterator<Integer> {
    // Index pointing to the mod
    private int modIndex;
    // Number of visible mods
    private int modCount;

    public ModLocationIterator() {
      modIndex = 0;
      modCount = countModsFromDots();
    }

    @Override
    public boolean hasNext() {
      if (modIndex < modCount) {
        return true;
      }
      if (modCount < 16) {
        return false;
      }
      else {
        // Scroll to next line
        if (dragOtherModsListOneLineUp()) {
          // If there are more mods
          modCount = countModsFromDots();
          modIndex = 12;
          return true;
        }
        else {
          // If no more mods
          return false;
        }
      }
    }

    @Override
    public Integer next() {
      int index = modIndex;
      AutomationUtil.click(LL_OTHER_MODS.get(index), "Clicking on other mod at index " + index);
      AutomationUtil.waitFor(250L);
      if (!ModScreen.waitForMinusButton()) {
        throw new ProcessException("Mod screen: minus button not found. Aborting.");
      }
      modIndex++;
      return index;
    }
  }

  private static class ModLocationIterable implements Iterable<Integer> {
    @Override
    public Iterator<Integer> iterator() {
      return new ModLocationIterator();
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(ModScreen.class);

  // Regex
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

  // Image patterns
  static {
    Configuration.configureImagePath();
  }
  private static final Pattern P_FILTER_AND_SORT_BUTTONS = new Pattern("mod_screen_filter_and_sort_buttons.png");
  private static final Pattern P_MINUS_BUTTON = new Pattern("mod_screen_minus_button.png");
  private static final Pattern P_UNASSIGNED_LABEL = new Pattern("mod_screen_unassigned.png");
  private static final Pattern P_MOD_DOT = new Pattern("mod_dot.png");
  private static final Pattern P_ASSIGN_MOD_REMOVE_BUTTON = new Pattern("mod_screen_assign_mod_remove.png");
  private static final Pattern P_ASSIGN_LOADOUT_ASSIGN_BUTTON = new Pattern("mod_screen_assign_loadout_assign.png");
  private static final Pattern P_REVERT_BUTTON = new Pattern("mod_screen_revert.png");
  private static final Pattern P_SCROLL_BAR_SINGLE_LINE = new Pattern("mod_screen_single_line_scroll.png");
  private static final Pattern P_DIALOG_BOX_OK = new Pattern("mod_screen_dialog_box_ok.png"); // For dry-run

  // Locations
  public static final Location L_BACK_BUTTON = new Location(44, 44);
  public static final Location L_FILTER_BUTTON = new Location(165, 141);
  public static final Map<ModSlot, Location> LM_CHAR_MOD_MAP = new LinkedHashMap<>();
  static {
    LM_CHAR_MOD_MAP.put(ModSlot.SQUARE, new Location(587, 185));
    LM_CHAR_MOD_MAP.put(ModSlot.ARROW, new Location(700, 165));
    LM_CHAR_MOD_MAP.put(ModSlot.DIAMOND, new Location(600, 370));
    LM_CHAR_MOD_MAP.put(ModSlot.TRIANGLE, new Location(686, 335));
    LM_CHAR_MOD_MAP.put(ModSlot.CIRCLE, new Location(600, 510));
    LM_CHAR_MOD_MAP.put(ModSlot.CROSS, new Location(700, 465));
  }
  public static final List<Location> LL_OTHER_MODS = new ArrayList<>(16);
  static {
    for (int iy = 0; iy < 4; iy++) {
      for (int ix = 0; ix < 4; ix++) {
        LL_OTHER_MODS.add(
                new Location(116 + ix * 100, 235 + iy * 120)
        );
      }
    }
  }
  public static final Location L_CONFIRM_BUTTON = new Location(1040, 604);

  // Regions
  public static final Region R_CHARACTER_NAME = new Region(172, 18, 500, 30);
  public static final Region R_CHARACTER_MOD_SET_AND_SLOT = new Region(793, 112, 370, 26);
  public static final Region R_CHARACTER_MOD_LEVEL_AND_TIER = new Region(870, 221, 48, 18);
  public static final Region R_CHARACTER_MOD_PRIMARY_STAT = new Region(991, 171, 200, 25);
  public static final List<Region> RL_CHARACTER_MOD_SECONDARY_STATS = Arrays.asList(
          new Region(991, 224, 200, 25),
          new Region(991, 247, 200, 25),
          new Region(991, 271, 200, 25),
          new Region(991, 295, 200, 25)
  );
  public static final Supplier<DotsTierSetAndSlot> RS_CHARACTER_MOD_SET_AND_SLOT = () -> extractModDotsTierSetAndSlot(true);
  public static final Supplier<String> RS_CHARACTER_MOD_PRIMARY_STAT_STRING = () -> AutomationUtil.readLine(R_CHARACTER_MOD_PRIMARY_STAT);
  public static final List<Supplier<String>> RSL_CHARACTER_MOD_SECONDARY_STAT_STRINGS = RL_CHARACTER_MOD_SECONDARY_STATS
          .stream()
          .<Supplier<String>>map(
                  region -> (
                          () -> AutomationUtil.readLine(region)
                  )
          )
          .collect(Collectors.toList());
  public static final Region R_OTHER_MOD_SET_AND_SLOT = new Region(793, 420, 424, 26);
  public static final Region R_OTHER_MOD_LEVEL_AND_TIER = new Region(870, 528, 48, 18);
  public static final Region R_OTHER_MOD_PRIMARY_STAT = new Region(991, 478, 200, 25);
  public static final List<Region> RL_OTHER_MOD_SECONDARY_STATS = Arrays.asList(
          new Region(991, 531, 200, 25),
          new Region(991, 554, 200, 25),
          new Region(991, 578, 200, 25),
          new Region(991, 602, 200, 25)
  );
  public static final Supplier<DotsTierSetAndSlot> RS_OTHER_MOD_SET_AND_SLOT = () -> extractModDotsTierSetAndSlot(false);
  public static final Supplier<String> RS_OTHER_MOD_PRIMARY_STAT_STRING = () -> AutomationUtil.readLine(R_OTHER_MOD_PRIMARY_STAT);
  public static final List<Supplier<String>> RSL_OTHER_MOD_SECONDARY_STAT_STRINGS = RL_OTHER_MOD_SECONDARY_STATS
          .stream()
          .<Supplier<String>>map(
                  region -> (
                          () -> AutomationUtil.readLine(region)
                  )
          )
          .collect(Collectors.toList());
  public static final Region R_FILTER_AND_SORT_BUTTONS = new Region(50, 104, 443, 75);
  public static final List<Region> RL_MOD_DOTS = new ArrayList<>(16);
  static {
    for (int iy = 0; iy < 4; iy++) {
      for (int ix = 0; ix < 4; ix++) {
        RL_MOD_DOTS.add(
                new Region(76 + ix * 99, 180 + iy * 119, 13, 25)
        );
      }
    }
  }
  public static final Region R_BELOW_FIRST_MOD_DOT = new Region(76, 205, 13, 22);
  public static final Region R_MINUS_BUTTON = new Region(1161, 92, 85, 84);
  public static final Region R_UNASSIGNED_LABEL = new Region(793, 110, 155, 30);
  public static final Region R_REMOVE_BUTTON = new Region(673, 452, 119, 42);
  public static final Region R_ASSIGN_LOADOUT_BUTTON = new Region(659, 554, 94, 38);
  public static final Region R_REVERT_BUTTON = new Region(854, 585, 100, 36);
  public static final Region R_MOD_SCROLLBAR = new Region(470, 182, 4, 432);
  public static final Region R_DIALOG_BOX_OK = new Region(767, 439, 51, 43); // For dry-run

  public static boolean waitForFilterAndSortButtons() {
    return AutomationUtil.waitForPattern(R_FILTER_AND_SORT_BUTTONS, P_FILTER_AND_SORT_BUTTONS, "Waiting for filter and sort buttons");
  }

  public static boolean waitForMinusButton() {
    return AutomationUtil.waitForPattern(R_MINUS_BUTTON, P_MINUS_BUTTON, "Waiting for minus button");
  }

  public static boolean waitForRevertButtonVanish() {
    return AutomationUtil.waitForPatternVanish(R_REVERT_BUTTON, P_REVERT_BUTTON, "Waiting for remove button to vanish");
  }

  public static boolean checkName(String name) {
    String readName = AutomationUtil.readLine(R_CHARACTER_NAME);
    int score = FuzzySearch.ratio(StringUtil.prepareForMatching(name), StringUtil.prepareForMatching(readName));
    boolean isPassed = score > 85;
    LOG.info("Checked name: {}. Read on screen: {}. Score: {}. Passed: {}", name, readName, score, isPassed);
    return isPassed;
  }

  public static boolean checkForMinusButton() {
    return AutomationUtil.checkForPattern(R_MINUS_BUTTON, P_MINUS_BUTTON, "Waiting for minus button");
  }

  public static boolean checkForUnassignedLabel() {
    return AutomationUtil.checkForPattern(R_UNASSIGNED_LABEL, P_UNASSIGNED_LABEL, "Checking for unassigned label");
  }

  public static boolean checkForRevertButton() {
    return AutomationUtil.checkForPattern(R_REVERT_BUTTON, P_REVERT_BUTTON, "Checking for revert button");
  }

  public static boolean checkForDialogBoxOk() {
    return AutomationUtil.checkForPattern(R_DIALOG_BOX_OK, P_DIALOG_BOX_OK, "Checking for OK button in dialog box");
  }

  public static StateAfterModMoveOrder waitAndGetStateAfterModMoveOrder() {
    LOG.info("Checking state after mod move order");
    long startTimeMillis = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTimeMillis < 5000L) {
      if (AutomationUtil.checkForPattern(R_REMOVE_BUTTON, P_ASSIGN_MOD_REMOVE_BUTTON, "Checking for mod remove button")) {
        LOG.info("State: REMOVE_BUTTON");
        return StateAfterModMoveOrder.REMOVE_BUTTON;
      }
      if (AutomationUtil.checkForPattern(R_ASSIGN_LOADOUT_BUTTON, P_ASSIGN_LOADOUT_ASSIGN_BUTTON, "Checking for loadout assign button")) {
        LOG.info("State: ASSIGN_LOADOUT_BUTTON");
        return StateAfterModMoveOrder.ASSIGN_LOADOUT_BUTTON;
      }
      if (AutomationUtil.checkForPattern(R_FILTER_AND_SORT_BUTTONS, P_FILTER_AND_SORT_BUTTONS, "Checking for filter and sort buttons")) {
        LOG.info("State: FILTER_AND_SORT_BUTTONS");
        return StateAfterModMoveOrder.FILTER_AND_SORT_BUTTONS;
      }
      AutomationUtil.waitFor(200L);
    }
    LOG.info("Timeout. State: NONE");
    return StateAfterModMoveOrder.NONE;
  }

  public static void enterModFilter() {
    AutomationUtil.click(L_FILTER_BUTTON, "Clicking on filter button");
  }

  public static void exitModScreen() {
    AutomationUtil.click(L_BACK_BUTTON, "Clicking on back button");
  }

  public static Iterable<Integer> readOtherModLocations() {
    return new ModLocationIterable();
  }

  public static void dragOtherModsToTop() {
    AutomationUtil.dragDrop(
            LL_OTHER_MODS.get(0),
            LL_OTHER_MODS.get(8),
            "Dragging mods to top"
    );
  }

  public static ModWithStatsInText extractModText(boolean isCharacter) {
    Region primaryStatRegion = isCharacter ? R_CHARACTER_MOD_PRIMARY_STAT : R_OTHER_MOD_PRIMARY_STAT;
    List<Region> secondaryStatRegions = isCharacter ? RL_CHARACTER_MOD_SECONDARY_STATS : RL_OTHER_MOD_SECONDARY_STATS;

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
    ModStat primaryStat = new ModStat(modWithStatsInText.getPrimaryStat(), InputType.GAME);
    List<ModStat> secondaryStats = modWithStatsInText.getSecondaryStats().stream()
            .map(s -> new ModStat(s, InputType.GAME))
            .collect(Collectors.toList());
    mod.setPrimaryStat(primaryStat);
    mod.setSecondaryStats(secondaryStats);

    return mod;
  }

  public static int countModsFromDots() {
    for (int i = 0; i < RL_MOD_DOTS.size(); i++) {
      if (!AutomationUtil.checkForPattern(RL_MOD_DOTS.get(i), P_MOD_DOT, "Checking dot for mod #" + i)) {
        return i;
      }
    }
    return RL_MOD_DOTS.size();
  }

  public static DotsTierSetAndSlot extractModDotsTierSetAndSlot(boolean isCharacter) {
    String text = AutomationUtil.readLine(isCharacter ? R_CHARACTER_MOD_SET_AND_SLOT : R_OTHER_MOD_SET_AND_SLOT);
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
    ModSlot slot = ModSlot.fromString(slotAsString, InputType.GAME);
    if (Stream.of(dots, tier, set, slot).anyMatch(Objects::isNull)) {
      return null;
    }
    return new DotsTierSetAndSlot(dots, tier, set, slot);
  }

  public static int extractModLevel(boolean isCharacter) {
    String text = AutomationUtil.readLine(isCharacter ? R_CHARACTER_MOD_LEVEL_AND_TIER : R_OTHER_MOD_LEVEL_AND_TIER);
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
    AutomationUtil.mouseMove(LL_OTHER_MODS.get(12), "Move mouse to 1st mod of 4th line");
    AutomationUtil.mouseDown(Button.LEFT, "Start drag");
    int mouseY = LL_OTHER_MODS.get(12).getY();
    int movement = -22;
    int halfwayY = (LL_OTHER_MODS.get(12).getY() + LL_OTHER_MODS.get(8).getY()) / 2;
    while (mouseY > halfwayY) {
      AutomationUtil.mouseMoveOffset(0, movement, "Move up: fast");
      mouseY += movement;
    }
    movement = -11;
    boolean isOK = false;
    while (mouseY > LL_OTHER_MODS.get(0).getY()) {
      AutomationUtil.mouseMoveOffset(0, movement, "Move up: medium");
      mouseY += movement;
      if (AutomationUtil.checkForPattern(R_BELOW_FIRST_MOD_DOT, P_MOD_DOT, "Check if dot is just below for top left mod")) {
        isOK = true;
        break;
      }
    }
    if (isOK) {
      movement = -5;
      isOK = false;
      while (mouseY > LL_OTHER_MODS.get(0).getY()) {
        AutomationUtil.mouseMoveOffset(0, movement, "Move up: slow");
        mouseY += movement;
        if (AutomationUtil.checkForPattern(RL_MOD_DOTS.get(0), P_MOD_DOT, "Check if dot is correctly placed for top left mod")) {
          isOK = true;
          break;
        }
      }
    }
    if (!isOK) {
      throw new ProcessException("Could not scroll up the mod list");
    }
    AutomationUtil.mouseUp("End drag");
    AutomationUtil.waitFor(500L);

    return AutomationUtil.checkForPattern(RL_MOD_DOTS.get(0), P_MOD_DOT, "Check if drag has been correctly performed");
  }

  public static double computeModProgress() {
    List<Match> matches = AutomationUtil.findAllPatterns(R_MOD_SCROLLBAR, P_SCROLL_BAR_SINGLE_LINE, "Finding lines in mod scroll bar")
            .stream()
            .sorted(Comparator.comparing(Match::getY))
            .collect(Collectors.toList());
    if (matches.size() < 2) {
      return 1d;
    }
    int topHighlightY = matches.get(0).getY();
    int bottomHightlightY = matches.get(matches.size() - 1).getY();
    int highlightedScrollHeight = bottomHightlightY - topHighlightY;
    int effectiveScrollbarHeight = AutomationUtil.getShiftedRegion(R_MOD_SCROLLBAR).getH() - highlightedScrollHeight;
    int effectiveHighlightPos = topHighlightY - AutomationUtil.getShiftedRegion(R_MOD_SCROLLBAR).getY();
    return (double)effectiveHighlightPos / (double)effectiveScrollbarHeight;
  }

  private static boolean fuzzyModStatTextMatch(ModStat referenceModStat, String otherModStatText) {
    String referenceModStatString = StringUtil.stripSpaces(referenceModStat.toString());
    String otherModStatTextStripped = StringUtil.stripSpaces(otherModStatText);
    int score = FuzzySearch.ratio(referenceModStatString, otherModStatTextStripped);
    boolean result = (score >= StringUtil.MOD_STAT_MATCH_THRESHOLD);
    LOG.debug("Matching mod: {} with text: \"{}\", score:{}, result: {}", referenceModStat, otherModStatText, score, result);
    return result;
  }

  public static boolean matchMods(com.charlie.swgoh.datamodel.xml.Mod referenceMod, boolean isCharacter) {
    Supplier<ModScreen.DotsTierSetAndSlot> dotsTierSetAndSlotSupplier = isCharacter ? RS_CHARACTER_MOD_SET_AND_SLOT : RS_OTHER_MOD_SET_AND_SLOT;
    Supplier<String> primaryModStatTextSupplier = isCharacter ? RS_CHARACTER_MOD_PRIMARY_STAT_STRING : RS_OTHER_MOD_PRIMARY_STAT_STRING;
    List<Supplier<String>> secondaryModStatTextSuppliers = isCharacter ? RSL_CHARACTER_MOD_SECONDARY_STAT_STRINGS : RSL_OTHER_MOD_SECONDARY_STAT_STRINGS;

    if (referenceMod.getSecondaryStats().size() != secondaryModStatTextSuppliers.size()) {
      return false;
    }
    ModScreen.DotsTierSetAndSlot dotsTierSetAndSlot = dotsTierSetAndSlotSupplier.get();
    if (
            dotsTierSetAndSlot == null
                    || dotsTierSetAndSlot.getDots() != referenceMod.getDots()
                    || dotsTierSetAndSlot.getSlot() != referenceMod.getSlot()
                    || dotsTierSetAndSlot.getTier() != referenceMod.getTier()
                    || dotsTierSetAndSlot.getSet() != referenceMod.getSet()
    ) {
      return false;
    }
    String primaryStatText = primaryModStatTextSupplier.get();
    if (!fuzzyModStatTextMatch(referenceMod.getPrimaryStat(), primaryStatText)) {
      return false;
    }
    for (int i = 0; i < referenceMod.getSecondaryStats().size(); i++) {
      String secondaryStatText = secondaryModStatTextSuppliers.get(i).get();
      if (!fuzzyModStatTextMatch(referenceMod.getSecondaryStats().get(i), secondaryStatText)) {
        return false;
      }
    }

    return true;
  }

  public static Mod readOtherMod() {
    int level = extractModLevel(false);
    if (level < 15) {
      return null;
    }

    DotsTierSetAndSlot dotsTierSetAndSlot = extractModDotsTierSetAndSlot(false);
    if (dotsTierSetAndSlot == null || dotsTierSetAndSlot.getDots() < 5) {
      return null;
    }

    Mod mod = extractModStats(false);
    mod.setCharacter(null);
    mod.setSlot(dotsTierSetAndSlot.getSlot());
    mod.setSet(dotsTierSetAndSlot.getSet());
    mod.setDots(dotsTierSetAndSlot.getDots());
    mod.setLevel(level);
    mod.setTier(dotsTierSetAndSlot.getTier());
    return mod;
  }

}
