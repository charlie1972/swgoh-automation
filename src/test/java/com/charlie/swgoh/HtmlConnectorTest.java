package com.charlie.swgoh;

import com.charlie.swgoh.datamodel.*;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.connector.HtmlConnector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlConnectorTest {

  private static final String FILE = "src/test/resources/test-data.html";

  @Test
  public void unmarshallTest() {
    Map<String, List<Mod>> modMap = HtmlConnector.getModsByCharacterFromHTML(FILE);
    assertEquals(2, modMap.size());

    List<Mod> drModList = modMap.get("Darth Revan");
    assertEquals(6, drModList.size());

    List<Mod> bsfModList = modMap.get("Bastila Shan (Fallen)");
    assertEquals(3, bsfModList.size());

    Mod drModCross = drModList.stream().filter(mod -> mod.getSlot() == ModSlot.CROSS).findFirst().get();
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
  }

}
