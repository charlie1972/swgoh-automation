package com.charlie.swgoh;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.xml.Mod;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlConnectorTest {

  private static final String FILE = "src/test/resources/test-data.html";

  @Test
  public void unmarshallCharacterTest() {
    List<Mod> mods = HtmlConnector.getModsFromHTML(FILE, false);
    Map<String, List<Mod>> modMap = mods.stream().collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));

    assertEquals(3, modMap.size());

    List<Mod> arcModList = modMap.get("ARC Trooper");
    assertNotNull(arcModList);
    assertEquals(3, arcModList.size());

    assertTrue(modMap.containsKey("CT-21-0408 \"Echo\""));

    List<Mod> gasModList = modMap.get("General Skywalker");
    assertNotNull(gasModList);
    assertEquals(5, gasModList.size());

    assertFalse(gasModList.stream().map(Mod::getSlot).anyMatch(modSlot -> modSlot == ModSlot.ARROW));

    Mod gasModCross = gasModList.stream().filter(mod -> mod.getSlot() == ModSlot.CROSS).findFirst().get();
    assertEquals("General Kenobi", gasModCross.getFromCharacter());
    assertEquals(6, gasModCross.getDots());
    assertEquals(15, gasModCross.getLevel());
    assertEquals(ModSet.HEALTH, gasModCross.getSet());
    assertEquals(ModTier.D, gasModCross.getTier());
    assertEquals(new ModStat(0, "24", ModStatUnit.PROTECTION_PCT), gasModCross.getPrimaryStat());
    assertEquals(
            Stream.of(
                    new ModStat(2, "3.7", ModStatUnit.HEALTH_PCT),
                    new ModStat(2, "74", ModStatUnit.OFFENSE_FLAT),
                    new ModStat(2, "28", ModStatUnit.DEFENSE_FLAT),
                    new ModStat(3, "16", ModStatUnit.SPEED)
            ).collect(Collectors.toList()),
            gasModCross.getSecondaryStats()
    );
  }

  @Test
  public void unmarshallFromCharacterTest() {
    List<Mod> mods = HtmlConnector.getModsFromHTML(FILE, false);
    Map<String, List<Mod>> modMap = mods.stream()
            .filter(mod -> !mod.getFromCharacter().isEmpty())
            .collect(Collectors.groupingBy(Mod::getFromCharacter, LinkedHashMap::new, Collectors.toList()));

    assertEquals(8, modMap.size());

    List<Mod> hkModList = modMap.get("HK-47");
    assertNotNull(hkModList);
    assertEquals(2, hkModList.size());

    List<Mod> bsfModList = modMap.get("Bastila Shan (Fallen)");
    assertNotNull(bsfModList);
    assertEquals(1, bsfModList.size());
  }

}
