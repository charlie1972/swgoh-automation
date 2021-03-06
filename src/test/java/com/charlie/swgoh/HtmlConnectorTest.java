package com.charlie.swgoh;

import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.datamodel.xml.Mods;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlConnectorTest {

  private static final String FILE = "src/test/resources/test-data.html";

  @Test
  public void unmarshallCharacterTest() {
    List<Mod> mods = HtmlConnector.getModsFromHTML(FILE);
    Map<String, List<Mod>> modMap = mods.stream().collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));

    assertEquals(2, modMap.size());

    List<Mod> drModList = modMap.get("Darth Revan");
    assertEquals(6, drModList.size());

    List<Mod> bsfModList = modMap.get("Bastila Shan (Fallen)");
    assertEquals(3, bsfModList.size());

    Mod drModCross = drModList.stream().filter(mod -> mod.getSlot() == ModSlot.CROSS).findFirst().get();
    assertEquals("Jedi Knight Revan", drModCross.getFromCharacter());
    assertEquals(6, drModCross.getDots());
    assertEquals(15, drModCross.getLevel());
    assertEquals(ModSet.SPEED, drModCross.getSet());
    assertEquals(ModTier.D, drModCross.getTier());
    assertEquals(new ModStat(0, "30", ModStatUnit.POTENCY), drModCross.getPrimaryStat());
    assertEquals(
            Stream.of(
                    new ModStat(5, "26", ModStatUnit.SPEED),
                    new ModStat(1, "1.37", ModStatUnit.OFFENSE_PCT),
                    new ModStat(1, "1.48", ModStatUnit.CRIT_CHANCE_PCT),
                    new ModStat(2, "26", ModStatUnit.DEFENSE_FLAT)
            ).collect(Collectors.toList()),
            drModCross.getSecondaryStats()
    );

    Mod bsfModDiamond = bsfModList.stream().filter(mod -> mod.getSlot() == ModSlot.DIAMOND).findFirst().get();
    assertEquals("", bsfModDiamond.getFromCharacter());
  }

  @Test
  public void unmarshallFromCharacterTest() {
    List<Mod> mods = HtmlConnector.getModsFromHTML(FILE);
    Map<String, List<Mod>> modMap = mods.stream()
            .filter(mod -> !mod.getFromCharacter().isEmpty())
            .collect(Collectors.groupingBy(Mod::getFromCharacter, LinkedHashMap::new, Collectors.toList()));

    assertEquals(2, modMap.size());

    List<Mod> drModList = modMap.get("Jedi Knight Revan");
    assertEquals(6, drModList.size());

    List<Mod> bsfModList = modMap.get("Bastila Shan");
    assertEquals(2, bsfModList.size());
  }

}
