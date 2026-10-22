package org.openpatch.scratch.internal.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ImageTurnTest {

  private static final int NOSE = 0xffff0000;
  private static final int BODY = 0xff0000ff;

  /**
   * A 3 wide, 2 high arrow: the nose pixel sits in the middle of the given edge, everything else is
   * body.
   */
  private static Image arrow(String nose) {
    int[] pixels = new int[3 * 2];
    Arrays.fill(pixels, BODY);
    switch (nose) {
      case "up" -> pixels[1] = NOSE;
      case "down" -> pixels[3 + 1] = NOSE;
      case "left" -> pixels[0] = NOSE;
      default -> throw new IllegalArgumentException(nose);
    }
    return new Image(3, 2, pixels);
  }

  private static int at(Image image, int x, int y) {
    return image.get(x, y);
  }

  @Test
  void aPictureFacingRightIsLeftAlone() {
    Image image = arrow("left");
    assertSame(image, Images.turnToFaceRight(image, 90));
  }

  @Test
  void aPictureFacingUpIsTurnedClockwise() {
    Image turned = Images.turnToFaceRight(arrow("up"), 0);

    assertEquals(2, turned.getWidth());
    assertEquals(3, turned.getHeight());
    // the nose was in the middle of the top edge, now of the right one
    assertEquals(NOSE, at(turned, 1, 1));
    assertEquals(BODY, at(turned, 0, 1));
  }

  @Test
  void aPictureFacingDownIsTurnedAnticlockwise() {
    Image turned = Images.turnToFaceRight(arrow("down"), 180);

    assertEquals(2, turned.getWidth());
    assertEquals(3, turned.getHeight());
    assertEquals(NOSE, at(turned, 1, 1));
    assertEquals(BODY, at(turned, 0, 1));
  }

  @Test
  void aPictureFacingLeftIsMirroredNotTurned() {
    Image turned = Images.turnToFaceRight(arrow("left"), -90);

    assertEquals(3, turned.getWidth());
    assertEquals(2, turned.getHeight());
    assertEquals(NOSE, at(turned, 2, 0));
    assertEquals(BODY, at(turned, 0, 0));
    // still upright: the bottom row did not move up
    assertEquals(BODY, at(turned, 2, 1));
  }

  @Test
  void directionsWrapAroundLikeScratchDirections() {
    assertEquals(NOSE, at(Images.turnToFaceRight(arrow("left"), 270), 2, 0));
    assertEquals(NOSE, at(Images.turnToFaceRight(arrow("down"), -180), 1, 1));
    assertEquals(NOSE, at(Images.turnToFaceRight(arrow("up"), 360), 1, 1));
  }

  @Test
  void aSlantedDirectionIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> Images.turnToFaceRight(arrow("up"), 45));
  }
}
