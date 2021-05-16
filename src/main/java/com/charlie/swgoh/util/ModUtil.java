package com.charlie.swgoh.util;

import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.json.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ModUtil {

  private ModUtil() {}

  private static final Logger LOG = LoggerFactory.getLogger(ModUtil.class);

  public static final DecimalFormat FORMAT = new DecimalFormat("+####0.##", new DecimalFormatSymbols(Locale.US));

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static Mod convertToJsonMod(com.charlie.swgoh.datamodel.xml.Mod xmlMod) {
    Mod jsonMod = new Mod();

    jsonMod.setPrimaryBonusType(xmlMod.getPrimaryStat().getUnit().getJsonString());
    jsonMod.setPrimaryBonusValue(FORMAT.format(xmlMod.getPrimaryStat().getValue()));

    jsonMod.setSecondaryType1(xmlMod.getSecondaryStats().get(0).getUnit().getJsonString());
    jsonMod.setSecondaryValue1(FORMAT.format(xmlMod.getSecondaryStats().get(0).getValue()));
    jsonMod.setSecondaryRoll1(xmlMod.getSecondaryStats().get(0).getRolls());

    if (xmlMod.getSecondaryStats().size() > 1) {
      jsonMod.setSecondaryType2(xmlMod.getSecondaryStats().get(1).getUnit().getJsonString());
      jsonMod.setSecondaryValue2(FORMAT.format(xmlMod.getSecondaryStats().get(1).getValue()));
      jsonMod.setSecondaryRoll2(xmlMod.getSecondaryStats().get(1).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 2) {
      jsonMod.setSecondaryType3(xmlMod.getSecondaryStats().get(2).getUnit().getJsonString());
      jsonMod.setSecondaryValue3(FORMAT.format(xmlMod.getSecondaryStats().get(2).getValue()));
      jsonMod.setSecondaryRoll3(xmlMod.getSecondaryStats().get(2).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 3) {
      jsonMod.setSecondaryType4(xmlMod.getSecondaryStats().get(3).getUnit().getJsonString());
      jsonMod.setSecondaryValue4(FORMAT.format(xmlMod.getSecondaryStats().get(3).getValue()));
      jsonMod.setSecondaryRoll4(xmlMod.getSecondaryStats().get(3).getRolls());
    }

    jsonMod.setSlot(xmlMod.getSlot().getShape());
    jsonMod.setSet(xmlMod.getSet().toJsonString());
    jsonMod.setLevel(xmlMod.getLevel());
    jsonMod.setPips(xmlMod.getDots());
    jsonMod.setCharacterID(null);
    jsonMod.setTier(xmlMod.getTier().toJsonInt());

    jsonMod.setUid(computeUid(jsonMod));

    return jsonMod;
  }

  public static com.charlie.swgoh.datamodel.xml.Mod convertToXmlMod(Mod jsonMod, Map<String, String> unitIdMap) {
    com.charlie.swgoh.datamodel.xml.Mod xmlMod = new com.charlie.swgoh.datamodel.xml.Mod();

    xmlMod.setPrimaryStat(getModStatFromJson(0, jsonMod.getPrimaryBonusValue(), jsonMod.getPrimaryBonusType()));

    List<ModStat> secondaryStats = new ArrayList<>();
    secondaryStats.add(getModStatFromJson(jsonMod.getSecondaryRoll1(), jsonMod.getSecondaryValue1(), jsonMod.getSecondaryType1()));
    secondaryStats.add(getModStatFromJson(jsonMod.getSecondaryRoll2(), jsonMod.getSecondaryValue2(), jsonMod.getSecondaryType2()));
    secondaryStats.add(getModStatFromJson(jsonMod.getSecondaryRoll3(), jsonMod.getSecondaryValue3(), jsonMod.getSecondaryType3()));
    secondaryStats.add(getModStatFromJson(jsonMod.getSecondaryRoll4(), jsonMod.getSecondaryValue4(), jsonMod.getSecondaryType4()));
    xmlMod.setSecondaryStats(secondaryStats);

    xmlMod.setSet(ModSet.fromString(jsonMod.getSet()));
    xmlMod.setSlot(ModSlot.fromString(jsonMod.getSlot(), InputType.JSON));
    xmlMod.setTier(ModTier.fromJsonInt(jsonMod.getTier()));
    xmlMod.setLevel(jsonMod.getLevel());
    xmlMod.setDots(jsonMod.getPips());
    xmlMod.setCharacter(unitIdMap.get(jsonMod.getCharacterID()));
    xmlMod.setFromCharacter("");

    return xmlMod;
  }

  private static String computeUid(Mod mod) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException e) {
      throw new ProcessException("Exception while creating SHA-256 MessageDigest. " + e.getClass().getName() + ": " + e.getMessage());
    }

    // Serialize mod to JSON byte array
    byte[] bytes;
    try {
      bytes = MAPPER.writeValueAsBytes(mod);
    }
    catch (JsonProcessingException e) {
      throw new ProcessException("Exception while serializing Mod. " + e.getClass().getName() + ": " + e.getMessage());
    }

    // Compute SHA-256 hash of the byte array
    byte[] hash = md.digest(bytes);

    // Keep the first half (128 bits -> 16 bytes)
    byte[] firstHalf = new byte[16];
    System.arraycopy(hash, 0, firstHalf, 0, 16);

    // Encode to Base64
    return Base64.getUrlEncoder().withoutPadding().encodeToString(firstHalf);
  }

  private static ModStat getModStatFromJson(int rolls, String value, String unit) {
    ModStatUnit modStatUnit = ModStatUnit.fromString(unit, InputType.JSON);
    double modStatValue = Math.round(100d * Double.parseDouble(value)) / 100d;
    return new ModStat(rolls, modStatValue, modStatUnit);
  }

}
