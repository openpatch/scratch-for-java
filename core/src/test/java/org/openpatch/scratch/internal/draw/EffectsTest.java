package org.openpatch.scratch.internal.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The colour effect on the CPU, which must match Scratch's own shader (sprite.frag in
 * scratch-render): too dark or too grey to carry a hue gets a little forced onto it, so the
 * shift is visible on white and black too.
 */
class EffectsTest {

  private static final int WHITE = 0xffffffff;
  private static final int BLACK = 0xff000000;
  private static final int GREY = 0xff808080;
  private static final int RED = 0xffff0000;

  @Test
  void noEffectLeavesEveryColourAlone() {
    Effects none = new Effects(0, 0, 0xffffffff);
    assertEquals(WHITE, none.apply(WHITE));
    assertEquals(BLACK, none.apply(BLACK));
    assertEquals(GREY, none.apply(GREY));
    assertEquals(RED, none.apply(RED));
  }

  @Test
  void aThirdOfTheWheelTurnsRedIntoGreen() {
    Effects e = new Effects(200.0 / 3, 0, 0xffffffff);
    assertEquals(0xff00ff00, e.apply(RED));
  }

  @Test
  void whitePicksUpAFaintColourLikeScratch() {
    // Scratch forces white to 9% saturation before the turn: a quarter round gives (244, 255, 232)
    Effects e = new Effects(50, 0, 0xffffffff);
    assertEquals(0xfff4ffe8, e.apply(WHITE));
  }

  @Test
  void greyPicksUpAFaintColourLikeScratch() {
    Effects e = new Effects(50, 0, 0xffffffff);
    int out = e.apply(GREY);
    assertEquals(122, (out >> 16) & 0xff, "grey keeps its brightness in red");
    assertEquals(128, (out >> 8) & 0xff, "grey keeps its brightness in green");
    assertEquals(116, out & 0xff, "grey keeps its brightness in blue");
  }

  @Test
  void blackPicksUpAFaintColourLikeScratch() {
    // Scratch lifts black to (hue 0, full saturation, 5.5% brightness) before the turn
    Effects e = new Effects(50, 0, 0xffffffff);
    int out = e.apply(BLACK);
    assertEquals(7, (out >> 16) & 0xff, "a quarter round from red is green sector");
    assertEquals(14, (out >> 8) & 0xff);
    assertEquals(0, out & 0xff);
  }
}
