package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.ModStat;
import com.charlie.swgoh.datamodel.ModStatUnit;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModScreenFilter {

  private ModScreenFilter() {}

  private static final Logger LOG = LoggerFactory.getLogger(ModScreenFilter.class);

  // Image patterns
  static {
    Configuration.configureImagePath();
  }
  private static final Pattern P_TITLE = new Pattern("mod_screen_filter_title.png");
  private static final Pattern P_UNASSIGNED_CHECKBOX_CHECKED = new Pattern("mod_screen_filter_unassigned_checked.png");
  private static final Pattern P_ANY_SLOT = new Pattern("mod_screen_filter_any_slot.png");
  private static final Pattern P_SECONDARY_STATS = new Pattern("mod_screen_filter_secondary_stats.png");

  // Locations
  public static final Location L_CLOSE = new Location(1200, 60);
  public static final Location L_DEFAULT = new Location(485, 640);
  public static final Location L_CONFIRM = new Location(1060, 640);
  public static final Location L_BOTTOM_FOR_SCROLL = new Location(350, 547);
  public static final Location L_TOP_FOR_SCROLL = new Location(350, 121);
  public static final Map<ModSlot, Location> LM_SLOTS = new LinkedHashMap<>();
  static {
    LM_SLOTS.put(ModSlot.SQUARE, new Location(519, 246));
    LM_SLOTS.put(ModSlot.ARROW, new Location(591, 246));
    LM_SLOTS.put(ModSlot.DIAMOND, new Location(663, 246));
    LM_SLOTS.put(ModSlot.TRIANGLE, new Location(735, 246));
    LM_SLOTS.put(ModSlot.CIRCLE, new Location(807, 246));
    LM_SLOTS.put(ModSlot.CROSS, new Location(879, 246));
  }
  public static final Map<ModSet, Location> LM_SETS = new LinkedHashMap<>();
  static {
    LM_SETS.put(ModSet.HEALTH, new Location(519, 376));
    LM_SETS.put(ModSet.DEFENSE, new Location(591, 376));
    LM_SETS.put(ModSet.CRIT_DAMAGE, new Location(663, 376));
    LM_SETS.put(ModSet.CRIT_CHANCE, new Location(735, 376));
    LM_SETS.put(ModSet.TENACITY, new Location(807, 376));
    LM_SETS.put(ModSet.OFFENSE, new Location(879, 376));
    LM_SETS.put(ModSet.POTENCY, new Location(951, 376));
    LM_SETS.put(ModSet.SPEED, new Location(1023, 376));
  }
  public static final Map<ModStatUnit, Location> LM_PRIMARY_STATS = new LinkedHashMap<>();
  static {
    LM_PRIMARY_STATS.put(ModStatUnit.ACCURACY_PCT, new Location(550, 490));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_AVOIDANCE_PCT, new Location(694, 490));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_CHANCE_PCT, new Location(838, 490));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_DAMAGE, new Location(982, 490));
    LM_PRIMARY_STATS.put(ModStatUnit.DEFENSE_PCT, new Location(1126, 490));

    LM_PRIMARY_STATS.put(ModStatUnit.HEALTH_PCT, new Location(406, 547));
    LM_PRIMARY_STATS.put(ModStatUnit.OFFENSE_PCT, new Location(550, 547));
    LM_PRIMARY_STATS.put(ModStatUnit.POTENCY, new Location(694, 547));
    LM_PRIMARY_STATS.put(ModStatUnit.PROTECTION_PCT, new Location(838, 547));
    LM_PRIMARY_STATS.put(ModStatUnit.SPEED, new Location(982, 547));
    LM_PRIMARY_STATS.put(ModStatUnit.TENACITY, new Location(1126, 547));
  }

  // Warning: these locations are relative to the top left of label "secondary stats (includes up to 4)" instead of window
  public static final Map<ModStatUnit, Location> LM_SECONDARY_STATS = new LinkedHashMap<>();
  static {
    LM_SECONDARY_STATS.put(ModStatUnit.CRIT_CHANCE_PCT, new Location(214, 110));
    LM_SECONDARY_STATS.put(ModStatUnit.DEFENSE_FLAT, new Location(358, 110));
    LM_SECONDARY_STATS.put(ModStatUnit.DEFENSE_PCT, new Location(502, 110));
    LM_SECONDARY_STATS.put(ModStatUnit.HEALTH_FLAT, new Location(646, 110));
    LM_SECONDARY_STATS.put(ModStatUnit.HEALTH_PCT, new Location(790, 110));

    LM_SECONDARY_STATS.put(ModStatUnit.OFFENSE_FLAT, new Location(70, 167));
    LM_SECONDARY_STATS.put(ModStatUnit.OFFENSE_PCT, new Location(214, 167));
    LM_SECONDARY_STATS.put(ModStatUnit.POTENCY, new Location(358, 167));
    LM_SECONDARY_STATS.put(ModStatUnit.PROTECTION_FLAT, new Location(502, 167));
    LM_SECONDARY_STATS.put(ModStatUnit.PROTECTION_PCT, new Location(646, 167));
    LM_SECONDARY_STATS.put(ModStatUnit.SPEED, new Location(790, 167));

    LM_SECONDARY_STATS.put(ModStatUnit.TENACITY, new Location(70, 224));
  }

  // Regions
  public static final Region R_TITLE = new Region(515, 45, 252, 41);
  public static final Region R_UNASSIGNED_CHECKBOX = new Region(334, 121, 46, 46);
  public static final Region R_ANY_SLOT = new Region(373, 232, 70, 32);
  public static final Region R_SECONDARY_STATS = new Region(338, 108, 8, 493);

  public static boolean waitForTitle() {
    return AutomationUtil.waitForPattern(R_TITLE, P_TITLE, "Waiting for title");
  }

  public static void ensureUnassignedIsUnchecked() {
    if (AutomationUtil.checkForPattern(R_UNASSIGNED_CHECKBOX, P_UNASSIGNED_CHECKBOX_CHECKED, "Checking if the unassigned checkbox is unchecked")) {
      // If pattern found => click on the checkbox
      AutomationUtil.click(R_UNASSIGNED_CHECKBOX, "Unchecking the unassigned checkbox");
    }
  }

  public static void ensureUnassignedIsChecked() {
    if (!AutomationUtil.checkForPattern(R_UNASSIGNED_CHECKBOX, P_UNASSIGNED_CHECKBOX_CHECKED, "Checking if the unassigned checkbox is checked")) {
      // If pattern not found => click on the checkbox
      AutomationUtil.click(R_UNASSIGNED_CHECKBOX, "Checking the unassigned checkbox");
    }
  }

  public static void clickDefaultAndEnsureAnySlotIsOnTop() {
    AutomationUtil.click(L_DEFAULT, "Clicking on default button");
    if (!AutomationUtil.checkForPattern(R_ANY_SLOT, P_ANY_SLOT, "Checking if Any Slot button is visible")) {
      AutomationUtil.dragDrop(LM_SLOTS.get(ModSlot.SQUARE), LM_SECONDARY_STATS.get(ModStatUnit.OFFENSE_PCT), "Drag & drop to make the Any Slot button visible");
    }
  }

  public static void filterForMod(Mod mod) {
    AutomationUtil.click(LM_SLOTS.get(mod.getSlot()), "Clicking on slot: " + mod.getSlot());
    AutomationUtil.click(LM_SETS.get(mod.getSet()), "Clicking on set: " + mod.getSet());

    ModStatUnit primaryStatUnit = mod.getPrimaryStat().getUnit();
    AutomationUtil.click(LM_PRIMARY_STATS.get(primaryStatUnit), "Clicking on primary stat: " + primaryStatUnit);

    dragUpToShowSecondaryStats();
    Location location = getTopLeftSecondaryStats();
    if (location == null) {
      throw new ProcessException("Could not locate the secondary stats label");
    }

    mod.getSecondaryStats().stream()
            .map(ModStat::getUnit)
            .map(LM_SECONDARY_STATS::get)
            .sorted((loc1, loc2) -> -loc1.compareTo(loc2))
            .forEach(loc -> AutomationUtil.click(new Location(location.x + loc.x, location.y + loc.y), "Secondary stat"));
  }

  public static void closeWithoutConfirm() {
    AutomationUtil.click(L_CLOSE, "Clicking on close button");
  }

  public static void confirm() {
    AutomationUtil.click(L_CONFIRM, "Clicking on confirm button");
  }

  public static void dragUpToShowSecondaryStats() {
    AutomationUtil.mouseMove(L_BOTTOM_FOR_SCROLL, "Moving mouse to starting position");
    AutomationUtil.dragDrop(L_BOTTOM_FOR_SCROLL, L_TOP_FOR_SCROLL, "Drag");
  }

  public static Location getTopLeftSecondaryStats() {
    Match match = AutomationUtil.findPattern(R_SECONDARY_STATS, P_SECONDARY_STATS, "Find Secondary Stats label");
    if (match != null) {
      return AutomationUtil.getReverseShiftedLocation(new Location(match.x, match.y));
    }
    else {
      return null;
    }
  }

}
