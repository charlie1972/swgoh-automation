package com.charlie.swgoh;

import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.Mod;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonConnectorTest {

  private static final String FILE_IN = "src/test/resources/test-optimizer-data.json";
  private static final String FILE_OUT = "target/test-optimizer-data-out.json";

  @Test
  public void deserializeTest() throws Exception {
    Progress progress = JsonConnector.readObjectFromFile(FILE_IN, Progress.class);
    assertNotNull(progress);
    assertNotNull(progress.getProfiles());
    assertEquals(1, progress.getProfiles().size());
    assertNotNull(progress.getProfiles().get(0));
    assertEquals("671416551", progress.getProfiles().get(0).getAllyCode());
    assertEquals("Charlie66", progress.getProfiles().get(0).getPlayerName());
    assertNotNull(progress.getProfiles().get(0).getMods());
    assertEquals(672, progress.getProfiles().get(0).getMods().size());
    Mod mod = progress.getProfiles().get(0).getMods().get(0);
    assertNotNull(mod);
    assertEquals("Defense", mod.getSecondaryType2());
    assertEquals("+22", mod.getSecondaryValue2());
    assertEquals(3, mod.getSecondaryRoll2());
  }

  @Test
  public void serializeTest() throws Exception {
    Progress progress = new Progress();
    progress.setProfiles(new ArrayList<>());
    progress.getProfiles().add(new Profile());
    progress.getProfiles().get(0).setAllyCode("123456789");
    progress.getProfiles().get(0).setPlayerName("ThePlayer");
    progress.getProfiles().get(0).setMods(new ArrayList<>());
    progress.getProfiles().get(0).getMods().add(new Mod());
    progress.getProfiles().get(0).getMods().get(0).setLevel(15);
    progress.getProfiles().get(0).getMods().get(0).setPips(5);
    progress.getProfiles().get(0).getMods().get(0).setPrimaryBonusType("speed");
    progress.getProfiles().get(0).getMods().get(0).setPrimaryBonusValue("30");
    JsonConnector.writeObjectToFile(progress, FILE_OUT);
    Progress loadedProgress = JsonConnector.readObjectFromFile(FILE_OUT, Progress.class);
    assertEquals(progress, loadedProgress);
  }

}
