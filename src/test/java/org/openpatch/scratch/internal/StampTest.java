package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.RotationStyle;
import processing.core.PImage;

/** The stage skips stamps outside the camera's view; only those may be skipped. */
class StampTest {

  private static Image tile() {
    return new Image("tile", new PImage(16, 16));
  }

  @Test
  void aStampInTheAreaOverlapsIt() {
    Stamp stamp = new Stamp(tile(), 0, 0);
    assertTrue(stamp.overlaps(-100, 100, -50, 50));
  }

  @Test
  void aStampFarAwayDoesNotOverlap() {
    assertFalse(new Stamp(tile(), 500, 0).overlaps(-100, 100, -50, 50), "right of it");
    assertFalse(new Stamp(tile(), -500, 0).overlaps(-100, 100, -50, 50), "left of it");
    assertFalse(new Stamp(tile(), 0, 300).overlaps(-100, 100, -50, 50), "above it");
    assertFalse(new Stamp(tile(), 0, -300).overlaps(-100, 100, -50, 50), "below it");
  }

  @Test
  void aStampJustOverTheEdgeStillOverlaps() {
    // Its centre lies outside, but half the tile reaches into the area.
    assertTrue(new Stamp(tile(), 107, 0).overlaps(-100, 100, -50, 50));
    assertTrue(new Stamp(tile(), 0, -57).overlaps(-100, 100, -50, 50));
  }

  @Test
  void aTurnedStampReachesAsFarAsItsCorners() {
    // A long image reaches as far as its ends, whichever way it is turned.
    Image bar = new Image("bar", new PImage(64, 4));
    Stamp turned = new Stamp(bar, 0, 0, 80, RotationStyle.ALL_AROUND);
    assertTrue(turned.overlaps(-100, 100, -50, 50));
  }

  @Test
  void aStampTurnedAroundAFarCentreReachesFurther() {
    // Turning around its left edge, the image can swing 64 pixels out.
    Image bar = new Image("bar", new PImage(64, 4));
    Stamp swinging = new Stamp(bar, 0, 160, 0, RotationStyle.ALL_AROUND, 0, 2);
    assertTrue(swinging.overlaps(-100, 100, -50, 50), "160 - 64 is within 100 of the area");
    Stamp centred = new Stamp(bar, 0, 160, 0, RotationStyle.ALL_AROUND);
    assertFalse(centred.overlaps(-100, 100, -50, 50), "turning around its middle, it reaches 32");
  }
}
