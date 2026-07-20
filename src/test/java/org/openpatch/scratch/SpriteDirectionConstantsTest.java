package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SpriteDirectionConstantsTest {

  // Regression guard: these constants used to be swapped (DIRECTION_RIGHT=0,
  // DIRECTION_UP=90, DIRECTION_LEFT=180, DIRECTION_DOWN=270), contradicting
  // both real Scratch's direction convention and Sprite.move()'s own trig,
  // which treats direction 90 (the default, "facing right") as movement
  // along +x. The correct mapping is 0=up, 90=right, 180=down, 270=left.

  @Test
  void directionUpIsZeroDegrees() {
    assertEquals(0.0, Sprite.DIRECTION_UP);
  }

  @Test
  void directionRightIsNinetyDegrees() {
    assertEquals(90.0, Sprite.DIRECTION_RIGHT);
  }

  @Test
  void directionDownIsOneEightyDegrees() {
    assertEquals(180.0, Sprite.DIRECTION_DOWN);
  }

  @Test
  void directionLeftIsTwoSeventyDegrees() {
    assertEquals(270.0, Sprite.DIRECTION_LEFT);
  }
}
