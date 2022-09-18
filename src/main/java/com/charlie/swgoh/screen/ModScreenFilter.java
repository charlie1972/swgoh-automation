package com.charlie.swgoh.screen;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.ModStatUnit;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
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
  private static final Pattern P_UNASSIGNED_CHECKBOX_UNCHECKED = new Pattern("mod_screen_filter_unassigned_unchecked.png");
  private static final Pattern P_ANY_SLOT = new Pattern("mod_screen_filter_any_slot.png");

  // Locations
  public static final Location L_DEFAULT = new Location(485, 640);
  public static final Location L_CONFIRM = new Location(1060, 640);
  public static final Map<ModSlot, Location> LM_SLOTS = new LinkedHashMap<>();
  static {
    LM_SLOTS.put(ModSlot.SQUARE, new Location(405, 216));
    LM_SLOTS.put(ModSlot.ARROW, new Location(460, 216));
    LM_SLOTS.put(ModSlot.DIAMOND, new Location(515, 216));
    LM_SLOTS.put(ModSlot.TRIANGLE, new Location(570, 216));
    LM_SLOTS.put(ModSlot.CIRCLE, new Location(625, 216));
    LM_SLOTS.put(ModSlot.CROSS, new Location(680, 216));
  }
  public static final Map<ModSet, Location> LM_SETS = new LinkedHashMap<>();
  static {
    LM_SETS.put(ModSet.HEALTH, new Location(405, 277));
    LM_SETS.put(ModSet.DEFENSE, new Location(460, 277));
    LM_SETS.put(ModSet.CRIT_DAMAGE, new Location(515, 277));
    LM_SETS.put(ModSet.CRIT_CHANCE, new Location(570, 277));
    LM_SETS.put(ModSet.TENACITY, new Location(625, 277));
    LM_SETS.put(ModSet.OFFENSE, new Location(680, 277));
    LM_SETS.put(ModSet.POTENCY, new Location(735, 277));
    LM_SETS.put(ModSet.SPEED, new Location(790, 277));
  }
  public static final Map<ModStatUnit, Location> LM_PRIMARY_STATS = new LinkedHashMap<>();
  static {
    LM_PRIMARY_STATS.put(ModStatUnit.ACCURACY_PCT, new Location(433, 338));
    LM_PRIMARY_STATS.put(ModStatUnit.OFFENSE_PCT, new Location(433, 398));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_AVOIDANCE_PCT, new Location(545, 338));
    LM_PRIMARY_STATS.put(ModStatUnit.POTENCY, new Location(545, 398));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_CHANCE_PCT, new Location(657, 338));
    LM_PRIMARY_STATS.put(ModStatUnit.PROTECTION_PCT, new Location(657, 398));
    LM_PRIMARY_STATS.put(ModStatUnit.CRIT_DAMAGE, new Location(769, 338));
    LM_PRIMARY_STATS.put(ModStatUnit.SPEED, new Location(769, 398));
    LM_PRIMARY_STATS.put(ModStatUnit.DEFENSE_PCT, new Location(881, 338));
    LM_PRIMARY_STATS.put(ModStatUnit.TENACITY, new Location(881, 398));
    LM_PRIMARY_STATS.put(ModStatUnit.HEALTH_PCT, new Location(993, 338));
  }
  public static final Map<ModStatUnit, Location> LM_SECONDARY_STATS = new LinkedHashMap<>();
  static {
    LM_SECONDARY_STATS.put(ModStatUnit.CRIT_CHANCE_PCT, new Location(433, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.OFFENSE_PCT, new Location(433, 565));
    LM_SECONDARY_STATS.put(ModStatUnit.DEFENSE_FLAT, new Location(545, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.POTENCY, new Location(545, 565));
    LM_SECONDARY_STATS.put(ModStatUnit.DEFENSE_PCT, new Location(657, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.PROTECTION_FLAT, new Location(657, 565));
    LM_SECONDARY_STATS.put(ModStatUnit.HEALTH_FLAT, new Location(769, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.PROTECTION_PCT, new Location(769, 565));
    LM_SECONDARY_STATS.put(ModStatUnit.HEALTH_PCT, new Location(881, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.SPEED, new Location(881, 565));
    LM_SECONDARY_STATS.put(ModStatUnit.OFFENSE_FLAT, new Location(993, 510));
    LM_SECONDARY_STATS.put(ModStatUnit.TENACITY, new Location(993, 565));
  }

  // Regions
  public static final Region R_TITLE = new Region(515, 45, 252, 41);
  public static final Region R_UNASSIGNED_CHECKBOX = new Region(334, 121, 46, 46);
  public static final Region R_ANY_SLOT = new Region(373, 232, 70, 32);

  public static boolean waitForTitle() {
    return AutomationUtil.waitForPattern(R_TITLE, P_TITLE, "Waiting for title");
  }

  public static void ensureUnassignedIsUnchecked() {
    if (!AutomationUtil.checkForPattern(R_UNASSIGNED_CHECKBOX, P_UNASSIGNED_CHECKBOX_UNCHECKED, "Checking if the unassigned checkbox is unchecked")) {
      // If pattern not found => click on the checkbox
      AutomationUtil.click(R_UNASSIGNED_CHECKBOX, "Unchecking the unassigned checkbox");
    }
  }

  public static void ensureUnassignedIsChecked() {
    if (AutomationUtil.checkForPattern(R_UNASSIGNED_CHECKBOX, P_UNASSIGNED_CHECKBOX_UNCHECKED, "Checking if the unassigned checkbox is checked")) {
      // If pattern found => click on the checkbox
      AutomationUtil.click(R_UNASSIGNED_CHECKBOX, "Checking the unassigned checkbox");
    }
  }

  public static void clickDefaultAndEnsureAnySlotIsOnTop() {
    AutomationUtil.click(L_DEFAULT, "Clicking on default button");
    if (!AutomationUtil.checkForPattern(R_ANY_SLOT, P_ANY_SLOT, "Checking if Any Slot button is visible")) {
      AutomationUtil.dragDrop(LM_SLOTS.get(ModSlot.SQUARE), LM_SECONDARY_STATS.get(ModStatUnit.OFFENSE_PCT), "Drag & drop to make the Any Slot button visible");
    }
  }

  public static void filterForModSlotAndSet(ModSlot slot, ModSet set) {
    clickDefaultAndEnsureAnySlotIsOnTop();
    AutomationUtil.click(LM_SLOTS.get(slot), "Clicking on slot: " + slot);
    AutomationUtil.click(LM_SETS.get(set), "Clicking on set: " + set);
  }

  public static void filterForMod(Mod mod) {
    filterForModSlotAndSet(mod.getSlot(), mod.getSet());

    ModStatUnit primaryStatUnit = mod.getPrimaryStat().getUnit();
    AutomationUtil.click(LM_PRIMARY_STATS.get(primaryStatUnit), "Clicking on primary stat: " + primaryStatUnit);

    mod.getSecondaryStats().stream()
            .sorted((stat1, stat2) -> LM_SECONDARY_STATS.get(stat2.getUnit()).compareTo(LM_SECONDARY_STATS.get(stat1.getUnit())))
            .forEach(modStat -> {
                      ModStatUnit secondaryStatUnit = modStat.getUnit();
                      AutomationUtil.click(LM_SECONDARY_STATS.get(secondaryStatUnit), "Clicking on secondary stat: " + secondaryStatUnit);
            });
  }

  public static void confirm() {
    AutomationUtil.click(L_CONFIRM, "Clicking on confirm button");
  }

}
