package com.charlie.swgoh.util;

import com.charlie.swgoh.datamodel.json.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.Locale;

public class ModUtil {

  private ModUtil() {}

  public static final DecimalFormat FORMAT = new DecimalFormat("+####0.##", new DecimalFormatSymbols(Locale.US));

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static Mod convertToJsonMod(com.charlie.swgoh.datamodel.xml.Mod xmlMod) {
    Mod jsonMod = new Mod();

    jsonMod.setPrimaryBonusType(xmlMod.getPrimaryStat().getUnit().toJsonString());
    jsonMod.setPrimaryBonusValue(ModUtil.FORMAT.format(xmlMod.getPrimaryStat().getValue()));

    jsonMod.setSecondaryType1(xmlMod.getSecondaryStats().get(0).getUnit().toJsonString());
    jsonMod.setSecondaryValue1(ModUtil.FORMAT.format(xmlMod.getSecondaryStats().get(0).getValue()));
    jsonMod.setSecondaryRoll1(xmlMod.getSecondaryStats().get(0).getRolls());

    if (xmlMod.getSecondaryStats().size() > 1) {
      jsonMod.setSecondaryType2(xmlMod.getSecondaryStats().get(1).getUnit().toJsonString());
      jsonMod.setSecondaryValue2(ModUtil.FORMAT.format(xmlMod.getSecondaryStats().get(1).getValue()));
      jsonMod.setSecondaryRoll2(xmlMod.getSecondaryStats().get(1).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 2) {
      jsonMod.setSecondaryType3(xmlMod.getSecondaryStats().get(2).getUnit().toJsonString());
      jsonMod.setSecondaryValue3(ModUtil.FORMAT.format(xmlMod.getSecondaryStats().get(2).getValue()));
      jsonMod.setSecondaryRoll3(xmlMod.getSecondaryStats().get(2).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 3) {
      jsonMod.setSecondaryType4(xmlMod.getSecondaryStats().get(3).getUnit().toJsonString());
      jsonMod.setSecondaryValue4(ModUtil.FORMAT.format(xmlMod.getSecondaryStats().get(3).getValue()));
      jsonMod.setSecondaryRoll4(xmlMod.getSecondaryStats().get(3).getRolls());
    }

    jsonMod.setSlot(xmlMod.getSlot().toJsonString());
    jsonMod.setSet(xmlMod.getSet().toJsonString());
    jsonMod.setLevel(xmlMod.getLevel());
    jsonMod.setPips(xmlMod.getDots());
    jsonMod.setCharacterID(null);
    jsonMod.setTier(xmlMod.getTier().toJsonInt());

    jsonMod.setUid(computeUid(jsonMod));

    return jsonMod;
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

}
