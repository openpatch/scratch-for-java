package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import processing.core.PConstants;
import processing.core.PImage;

class ImageTurnTest {

  private static final int NOSE = 0xffff0000;
  private static final int BODY = 0xff0000ff;

  /**
   * A 3 wide, 2 high arrow: the nose pixel sits in the middle of the given
   * edge, everything else is body.
   */
  private static PImage arrow(String nose) {
    PImage image = new PImage(3, 2, PConstants.ARGB);
    image.loadPixels();
    java.util.Arrays.fill(image.pixels, BODY);
    switch (nose) {
      case "up" -> image.pixels[1] = NOSE;
      case "down" -> image.pixels[3 + 1] = NOSE;
      case "left" -> image.pixels[0] = NOSE;
      default -> throw new IllegalArgumentException(nose);
    }
    image.updatePixels();
    return image;
  }

  private static int at(PImage image, int x, int y) {
    image.loadPixels();
    return image.pixels[y * image.width + x];
  }

  @Test
  void aPictureFacingRightIsLeftAlone() {
    PImage image = arrow("left");
    assertSame(image, Image.turnToFaceRight(image, 90));
  }

  @Test
  void aPictureFacingUpIsTurnedClockwise() {
    PImage turned = Image.turnToFaceRight(arrow("up"), 0);

    assertEquals(2, turned.width);
    assertEquals(3, turned.height);
    // the nose was in the middle of the top edge, now of the right one
    assertEquals(NOSE, at(turned, 1, 1));
    assertEquals(BODY, at(turned, 0, 1));
  }

  @Test
  void aPictureFacingDownIsTurnedAnticlockwise() {
    PImage turned = Image.turnToFaceRight(arrow("down"), 180);

    assertEquals(2, turned.width);
    assertEquals(3, turned.height);
    assertEquals(NOSE, at(turned, 1, 1));
    assertEquals(BODY, at(turned, 0, 1));
  }

  @Test
  void aPictureFacingLeftIsMirroredNotTurned() {
    PImage turned = Image.turnToFaceRight(arrow("left"), -90);

    assertEquals(3, turned.width);
    assertEquals(2, turned.height);
    assertEquals(NOSE, at(turned, 2, 0));
    assertEquals(BODY, at(turned, 0, 0));
    // still upright: the bottom row did not move up
    assertEquals(BODY, at(turned, 2, 1));
  }

  @Test
  void directionsWrapAroundLikeScratchDirections() {
    assertEquals(NOSE, at(Image.turnToFaceRight(arrow("left"), 270), 2, 0));
    assertEquals(NOSE, at(Image.turnToFaceRight(arrow("down"), -180), 1, 1));
    assertEquals(NOSE, at(Image.turnToFaceRight(arrow("up"), 360), 1, 1));
  }

  @Test
  void aSlantedDirectionIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> Image.turnToFaceRight(arrow("up"), 45));
  }
}
