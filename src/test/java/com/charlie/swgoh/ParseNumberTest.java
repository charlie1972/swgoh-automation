package com.charlie.swgoh;

import com.charlie.swgoh.screen.BronziumScreen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseNumberTest {

  @Test
  public void testSimpleNumberUnder1000() {
    Assertions.assertEquals(314, BronziumScreen.parseAllyPoints("314"));
  }

  @Test
  public void testSimpleNumberOver1000() {
    assertEquals(2141, BronziumScreen.parseAllyPoints("2,141"));
  }

  @Test
  public void testThousandsNumber() {
    assertEquals(3000, BronziumScreen.parseAllyPoints("3K"));
    assertEquals(3100, BronziumScreen.parseAllyPoints("3.1K"));
    assertEquals(3140, BronziumScreen.parseAllyPoints("3.14K"));
  }

  @Test
  public void testMillionsNumber() {
    assertEquals(3000000, BronziumScreen.parseAllyPoints("3M"));
    assertEquals(3100000, BronziumScreen.parseAllyPoints("3.1M"));
    assertEquals(3140000, BronziumScreen.parseAllyPoints("3.14M"));
  }

}
