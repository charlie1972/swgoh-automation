package com.charlie.swgoh;

import com.charlie.swgoh.util.AutomationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseNumberTest {

  @Test
  public void testSimpleNumberUnder1000() {
    Assertions.assertEquals(314, AutomationUtil.parseAllyPoints("314"));
  }

  @Test
  public void testSimpleNumberOver1000() {
    assertEquals(2141, AutomationUtil.parseAllyPoints("2,141"));
  }

  @Test
  public void testThousandsNumber() {
    assertEquals(3000, AutomationUtil.parseAllyPoints("3K"));
    assertEquals(3100, AutomationUtil.parseAllyPoints("3.1K"));
    assertEquals(3140, AutomationUtil.parseAllyPoints("3.14K"));
  }

  @Test
  public void testMillionsNumber() {
    assertEquals(3000000, AutomationUtil.parseAllyPoints("3M"));
    assertEquals(3100000, AutomationUtil.parseAllyPoints("3.1M"));
    assertEquals(3140000, AutomationUtil.parseAllyPoints("3.14M"));
  }

}
