package com.charlie.swgoh.screen;

import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.datamodel.ModSet;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.ModStatUnit;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ModScreenFilter {

  private ModScreenFilter() {}

  private static final Logger LOG = LoggerFactory.getLogger(ModScreenFilter.class);

  private static final long DELAY = 1000L;
  private static final Pattern TITLE = new Pattern("mod_screen_filter_title.png");
  private static final Pattern UNASSIGNED_CHECKBOX_UNCHECKED = new Pattern("mod_screen_filter_unassigned_unchecked.png");

  private static Location locUnassignedCheckbox;
  private static Location locConfirm;
  private static Location locDefault;
  private static Map<ModSlot, Location> locSlots;
  private static Map<ModSet, Location> locSets;
  private static Map<ModStatUnit, Location> locPrimaryStats;
  private static Map<ModStatUnit, Location> locSecondaryStats;
  private static Region regTitle;
  private static Region regUnassignedCheckbox;

  public static boolean waitForTitle() {
    return AutomationUtil.waitForPattern(getRegTitle(), TITLE, "Waiting for title");
  }

  public static void ensureUnassignedIsUnchecked() {
    if (!AutomationUtil.checkForPattern(getRegUnassignedCheckbox(), UNASSIGNED_CHECKBOX_UNCHECKED, "Checking if the unassigned checkbox is unchecked")) {
      // If pattern not found => click on the checkbox
      AutomationUtil.click(getLocUnassignedCheckbox(), "Unchecking the unassigned checkbox");
    }
  }

  public static void ensureUnassignedIsChecked() {
    if (AutomationUtil.checkForPattern(getRegUnassignedCheckbox(), UNASSIGNED_CHECKBOX_UNCHECKED, "Checking if the unassigned checkbox is checked")) {
      // If pattern found => click on the checkbox
      AutomationUtil.click(getLocUnassignedCheckbox(), "Checking the unassigned checkbox");
    }
  }

  public static void filterForModSlotAndSet(ModSlot slot, ModSet set) {
    AutomationUtil.click(getLocDefault(), "Clicking on default button");
    AutomationUtil.click(getLocSlots().get(slot), "Clicking on slot: " + slot);
    AutomationUtil.click(getLocSets().get(set), "Clicking on set: " + set);
  }

  public static void filterForMod(Mod mod) {
    filterForModSlotAndSet(mod.getSlot(), mod.getSet());

    ModStatUnit primaryStatUnit = mod.getPrimaryStat().getUnit();
    AutomationUtil.click(getLocPrimaryStats().get(primaryStatUnit), "Clicking on primary stat: " + primaryStatUnit);

    mod.getSecondaryStats().stream()
            .sorted((stat1, stat2) -> getLocSecondaryStats().get(stat2.getUnit()).compareTo(getLocSecondaryStats().get(stat1.getUnit())))
            .forEach(modStat -> {
                      ModStatUnit secondaryStatUnit = modStat.getUnit();
                      AutomationUtil.click(getLocSecondaryStats().get(secondaryStatUnit), "Clicking on secondary stat: " + secondaryStatUnit);
            });
  }

  public static void confirm() {
    AutomationUtil.click(getLocConfirm(), "Clicking on confirm button");
  }

  // Init

  public static void init() {
    locUnassignedCheckbox = AutomationUtil.getLocation(252, 151);
    locConfirm = AutomationUtil.getLocation(853, 634);
    locDefault = AutomationUtil.getLocation(422, 634);

    locSlots = new LinkedHashMap<>();
    locSlots.put(ModSlot.SQUARE, AutomationUtil.getLocation(405, 216));
    locSlots.put(ModSlot.ARROW, AutomationUtil.getLocation(460, 216));
    locSlots.put(ModSlot.DIAMOND, AutomationUtil.getLocation(515, 216));
    locSlots.put(ModSlot.TRIANGLE, AutomationUtil.getLocation(570, 216));
    locSlots.put(ModSlot.CIRCLE, AutomationUtil.getLocation(625, 216));
    locSlots.put(ModSlot.CROSS, AutomationUtil.getLocation(680, 216));

    locSets = new LinkedHashMap<>();
    locSets.put(ModSet.HEALTH, AutomationUtil.getLocation(405, 277));
    locSets.put(ModSet.DEFENSE, AutomationUtil.getLocation(460, 277));
    locSets.put(ModSet.CRIT_DAMAGE, AutomationUtil.getLocation(515, 277));
    locSets.put(ModSet.CRIT_CHANCE, AutomationUtil.getLocation(570, 277));
    locSets.put(ModSet.TENACITY, AutomationUtil.getLocation(625, 277));
    locSets.put(ModSet.OFFENSE, AutomationUtil.getLocation(680, 277));
    locSets.put(ModSet.POTENCY, AutomationUtil.getLocation(735, 277));
    locSets.put(ModSet.SPEED, AutomationUtil.getLocation(790, 277));

    locPrimaryStats = new LinkedHashMap<>();
    locPrimaryStats.put(ModStatUnit.ACCURACY_PCT, AutomationUtil.getLocation(433, 338));
    locPrimaryStats.put(ModStatUnit.OFFENSE_PCT, AutomationUtil.getLocation(433, 398));
    locPrimaryStats.put(ModStatUnit.CRIT_AVOIDANCE_PCT, AutomationUtil.getLocation(545, 338));
    locPrimaryStats.put(ModStatUnit.POTENCY, AutomationUtil.getLocation(545, 398));
    locPrimaryStats.put(ModStatUnit.CRIT_CHANCE_PCT, AutomationUtil.getLocation(657, 338));
    locPrimaryStats.put(ModStatUnit.PROTECTION_PCT, AutomationUtil.getLocation(657, 398));
    locPrimaryStats.put(ModStatUnit.CRIT_DAMAGE, AutomationUtil.getLocation(769, 338));
    locPrimaryStats.put(ModStatUnit.SPEED, AutomationUtil.getLocation(769, 398));
    locPrimaryStats.put(ModStatUnit.DEFENSE_PCT, AutomationUtil.getLocation(881, 338));
    locPrimaryStats.put(ModStatUnit.TENACITY, AutomationUtil.getLocation(881, 398));
    locPrimaryStats.put(ModStatUnit.HEALTH_PCT, AutomationUtil.getLocation(993, 338));

    locSecondaryStats = new LinkedHashMap<>();
    locSecondaryStats.put(ModStatUnit.CRIT_CHANCE_PCT, AutomationUtil.getLocation(433, 510));
    locSecondaryStats.put(ModStatUnit.OFFENSE_PCT, AutomationUtil.getLocation(433, 565));
    locSecondaryStats.put(ModStatUnit.DEFENSE_FLAT, AutomationUtil.getLocation(545, 510));
    locSecondaryStats.put(ModStatUnit.POTENCY, AutomationUtil.getLocation(545, 565));
    locSecondaryStats.put(ModStatUnit.DEFENSE_PCT, AutomationUtil.getLocation(657, 510));
    locSecondaryStats.put(ModStatUnit.PROTECTION_FLAT, AutomationUtil.getLocation(657, 565));
    locSecondaryStats.put(ModStatUnit.HEALTH_FLAT, AutomationUtil.getLocation(769, 510));
    locSecondaryStats.put(ModStatUnit.PROTECTION_PCT, AutomationUtil.getLocation(769, 565));
    locSecondaryStats.put(ModStatUnit.HEALTH_PCT, AutomationUtil.getLocation(881, 510));
    locSecondaryStats.put(ModStatUnit.SPEED, AutomationUtil.getLocation(881, 565));
    locSecondaryStats.put(ModStatUnit.OFFENSE_FLAT, AutomationUtil.getLocation(993, 510));
    locSecondaryStats.put(ModStatUnit.TENACITY, AutomationUtil.getLocation(993, 565));

    regTitle = AutomationUtil.getRegion(540, 51, 202, 48);
    regUnassignedCheckbox = AutomationUtil.getRegion(220, 120, 64, 64);
  }

  // Locations

  public static Location getLocUnassignedCheckbox() {
    return locUnassignedCheckbox;
  }

  public static Location getLocConfirm() {
    return locConfirm;
  }

  public static Location getLocDefault() {
    return locDefault;
  }

  public static Map<ModSlot, Location> getLocSlots() {
    return locSlots;
  }

  public static Map<ModSet, Location> getLocSets() {
    return locSets;
  }

  public static Map<ModStatUnit, Location> getLocPrimaryStats() {
    return locPrimaryStats;
  }

  public static Map<ModStatUnit, Location> getLocSecondaryStats() {
    return locSecondaryStats;
  }

  // Regions

  public static Region getRegTitle() {
    return regTitle;
  }

  public static Region getRegUnassignedCheckbox() {
    return regUnassignedCheckbox;
  }

}
