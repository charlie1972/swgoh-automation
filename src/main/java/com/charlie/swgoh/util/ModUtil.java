package com.charlie.swgoh.util;

import com.charlie.swgoh.datamodel.ModStat;
import com.charlie.swgoh.datamodel.ModWithStatsInText;
import com.charlie.swgoh.datamodel.json.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.ModScreen;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ModUtil {

  private ModUtil() {}

  private static final Logger LOG = LoggerFactory.getLogger(ModUtil.class);

  public static final DecimalFormat FORMAT = new DecimalFormat("+####0.##", new DecimalFormatSymbols(Locale.US));

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final int MOD_STAT_CERTAINTY_THRESHOLD = 90;
  private static final int MOD_STAT_PASSABLE_THRESHOLD = 50;

  public static Mod convertToJsonMod(com.charlie.swgoh.datamodel.xml.Mod xmlMod) {
    Mod jsonMod = new Mod();

    jsonMod.setPrimaryBonusType(xmlMod.getPrimaryStat().getUnit().toJsonString());
    jsonMod.setPrimaryBonusValue(FORMAT.format(xmlMod.getPrimaryStat().getValue()));

    jsonMod.setSecondaryType1(xmlMod.getSecondaryStats().get(0).getUnit().toJsonString());
    jsonMod.setSecondaryValue1(FORMAT.format(xmlMod.getSecondaryStats().get(0).getValue()));
    jsonMod.setSecondaryRoll1(xmlMod.getSecondaryStats().get(0).getRolls());

    if (xmlMod.getSecondaryStats().size() > 1) {
      jsonMod.setSecondaryType2(xmlMod.getSecondaryStats().get(1).getUnit().toJsonString());
      jsonMod.setSecondaryValue2(FORMAT.format(xmlMod.getSecondaryStats().get(1).getValue()));
      jsonMod.setSecondaryRoll2(xmlMod.getSecondaryStats().get(1).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 2) {
      jsonMod.setSecondaryType3(xmlMod.getSecondaryStats().get(2).getUnit().toJsonString());
      jsonMod.setSecondaryValue3(FORMAT.format(xmlMod.getSecondaryStats().get(2).getValue()));
      jsonMod.setSecondaryRoll3(xmlMod.getSecondaryStats().get(2).getRolls());
    }

    if (xmlMod.getSecondaryStats().size() > 3) {
      jsonMod.setSecondaryType4(xmlMod.getSecondaryStats().get(3).getUnit().toJsonString());
      jsonMod.setSecondaryValue4(FORMAT.format(xmlMod.getSecondaryStats().get(3).getValue()));
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

  public static boolean matchMods(com.charlie.swgoh.datamodel.xml.Mod referenceMod, ModWithStatsInText textMod) {
    LOG.debug("Fuzzy matching between this mod: {} and mod texts: {}", referenceMod, textMod);
    if (referenceMod.getSecondaryStats().size() != textMod.getSecondaryStats().size()) {
      LOG.debug("The secondary stats have different lengths. No match");
      return false;
    }

    List<Integer> scores = new ArrayList<>();
    scores.add(FuzzySearch.ratio(referenceMod.getPrimaryStat().toString(), StringUtil.stripSpaces(textMod.getPrimaryStat())));
    for (int i = 0; i < referenceMod.getSecondaryStats().size(); i++) {
      scores.add(FuzzySearch.ratio(referenceMod.getSecondaryStats().get(i).toString(), StringUtil.stripSpaces(textMod.getSecondaryStats().get(i))));
    }

    int nbPassable = (int) scores.stream().filter(score -> score >= MOD_STAT_PASSABLE_THRESHOLD).count();
    int nbCertain = (int) scores.stream().filter(score -> score >= MOD_STAT_CERTAINTY_THRESHOLD).count();
    // Criteria (both of them)
    // All passable
    // Certain: all except one, or all of them
    boolean result = (nbPassable == referenceMod.getSecondaryStats().size() + 1) && (nbCertain >= referenceMod.getSecondaryStats().size());
    LOG.debug("Scores: {}, passable: {}, certain: {}, result: {}", scores, nbPassable, nbCertain, result);
    return result;
  }

}
