package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ColorTest {

  @Test
  void rgbIsClamped() {
    Color c = new Color(300, -5, 128);
    assertEquals(255, c.getRed());
    assertEquals(0, c.getGreen());
    assertEquals(128, c.getBlue());
    assertEquals(255, c.getAlpha());
  }

  @Test
  void hexCodesInEveryForm() {
    assertEquals(new Color(255, 128, 0), new Color("#ff8000"));
    assertEquals(new Color(255, 136, 0), new Color("f80"));
    assertEquals(new Color(255, 128, 0, 128), new Color("#ff800080"));
    assertThrows(IllegalArgumentException.class, () -> new Color("orange"));
  }

  @Test
  void scratchHueGoesAround() {
    assertEquals(new Color(255, 0, 0), Color.fromHSB(0, 100, 100));
    assertEquals(new Color(0, 0, 255), Color.fromHSB(66.6667, 100, 100));
    assertEquals(Color.fromHSB(10, 100, 100), Color.fromHSB(110, 100, 100));
    assertEquals(Color.fromHSB(90, 100, 100), Color.fromHSB(-10, 100, 100));
  }

  @Test
  void hsbRoundTrips() {
    Color c = Color.fromHSB(42, 70, 80);
    assertEquals(42, c.getHue(), 0.5);
    assertEquals(70, c.getSaturation(), 0.5);
    assertEquals(80, c.getBrightness(), 0.5);
  }

  @Test
  void withMethodsLeaveTheOriginalAlone() {
    Color red = HtmlColor.RED;
    Color shifted = red.withHue(red.getHue() + 33.3333);
    assertEquals(new Color(255, 0, 0), HtmlColor.RED);
    assertNotEquals(red, shifted);
    assertEquals(new Color(0, 255, 0), shifted);
  }

  @Test
  void argbPacksAndUnpacks() {
    Color c = new Color(1, 2, 3, 4);
    assertEquals(0x04010203, c.toARGB());
    assertEquals(c, Color.fromARGB(0x04010203));
    assertEquals("#010203" + "04", c.toString());
  }

  @Test
  void greyHasNoHue() {
    assertEquals(0, new Color(128, 128, 128).getHue());
    assertEquals(0, new Color(128, 128, 128).getSaturation());
  }
}
