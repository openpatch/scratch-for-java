package org.openpatch.scratch.extensions.color;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ColorTest {

  private static final double DELTA = 1e-6;

  @Test
  void hexConstructorParsesRedGreenBlue() {
    Color color = new Color("#FF8000");

    assertEquals(255, color.getRed(), DELTA);
    assertEquals(128, color.getGreen(), DELTA);
    assertEquals(0, color.getBlue(), DELTA);
  }

  @Test
  void hexConstructorAcceptsCodeWithoutHash() {
    Color color = new Color("00FF00");

    assertEquals(0, color.getRed(), DELTA);
    assertEquals(255, color.getGreen(), DELTA);
    assertEquals(0, color.getBlue(), DELTA);
  }

  @Test
  void rgbConstructorSetsAllThreeChannelsIndependently() {
    // Regression guard: Color(double, double, double) used to call
    // setRGB(r, g, g) internally, silently overwriting the blue channel with
    // the green value instead of the value passed in.
    Color color = new Color(200.0, 100.0, 50.0);

    assertEquals(200, color.getRed(), DELTA);
    assertEquals(100, color.getGreen(), DELTA);
    assertEquals(50, color.getBlue(), DELTA);
  }

  @Test
  void setRgbUpdatesHsbFields() {
    Color color = new Color();
    color.setRGB(255, 0, 0);

    assertEquals(0, color.getH(), DELTA);
    assertEquals(255, color.getS(), DELTA);
    assertEquals(255, color.getL(), DELTA);
  }

  @Test
  void setHsbUpdatesRgbFields() {
    Color color = new Color(0.0);

    assertEquals(255, color.getRed(), DELTA);
    assertEquals(0, color.getGreen(), DELTA);
    assertEquals(0, color.getBlue(), DELTA);
  }

  @Test
  void changeColorAddsToCurrentHue() {
    Color color = new Color(0.0);
    color.changeColor(30.0);

    assertEquals(30, color.getH(), DELTA);
  }

  @Test
  void changeColorWrapsHueAbove255() {
    // From the class javadoc's own example: 285 => 30.
    Color color = new Color(0.0);
    color.changeColor(285.0);

    assertEquals(30, color.getH(), DELTA);
  }

  @Test
  void getReturnsPackedArgbHexValue() {
    Color color = new Color("#FF0000");

    assertEquals(0xffff0000, color.get());
  }

  @Test
  void getClampsOutOfRangeChannelsBeforePacking() {
    Color color = new Color();
    color.setRGB(300.0, -10.0, 128.0);

    assertEquals(0xffff0080, color.get());
  }

  @Test
  void copyConstructorCopiesAllChannels() {
    Color original = new Color("#336699");
    Color copy = new Color(original);

    assertEquals(original.getRed(), copy.getRed(), DELTA);
    assertEquals(original.getGreen(), copy.getGreen(), DELTA);
    assertEquals(original.getBlue(), copy.getBlue(), DELTA);
    assertEquals(original.getH(), copy.getH(), DELTA);
  }
}
