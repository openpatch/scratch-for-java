package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UtilsTest {

  private static final double DELTA = 1e-9;

  @Test
  void degreesToRadiansConvertsCorrectly() {
    assertEquals(0.0, Utils.degreesToRadians(0.0), DELTA);
    assertEquals(Math.PI, Utils.degreesToRadians(180.0), DELTA);
    assertEquals(2 * Math.PI, Utils.degreesToRadians(360.0), DELTA);
  }

  @Test
  void radiansToDegreesConvertsCorrectly() {
    assertEquals(0.0, Utils.radiansToDegrees(0.0), DELTA);
    assertEquals(180.0, Utils.radiansToDegrees(Math.PI), DELTA);
    assertEquals(360.0, Utils.radiansToDegrees(2 * Math.PI), DELTA);
  }

  @Test
  void rotateXYRotatesPointAroundOriginBy90Degrees() {
    double[] rotated = Utils.rotateXY(10.0, 0.0, 0.0, 0.0, 90.0);

    assertEquals(0.0, rotated[0], DELTA);
    assertEquals(10.0, rotated[1], DELTA);
  }

  @Test
  void rotateXYRotatesPointAroundNonOriginAnchor() {
    // Rotating (15, 5) by 90 degrees around anchor (5, 5) is equivalent to
    // rotating the offset (10, 0) around the origin, then re-adding the anchor.
    double[] rotated = Utils.rotateXY(15.0, 5.0, 5.0, 5.0, 90.0);

    assertEquals(5.0, rotated[0], DELTA);
    assertEquals(15.0, rotated[1], DELTA);
  }

  @Test
  void rotateXYWithZeroDegreesReturnsSamePoint() {
    double[] rotated = Utils.rotateXY(3.0, 4.0, 0.0, 0.0, 0.0);

    assertEquals(3.0, rotated[0], DELTA);
    assertEquals(4.0, rotated[1], DELTA);
  }

  @Test
  void rotateXYFullRotationReturnsSamePoint() {
    double[] rotated = Utils.rotateXY(3.0, 4.0, 1.0, 1.0, 360.0);

    assertEquals(3.0, rotated[0], DELTA);
    assertEquals(4.0, rotated[1], DELTA);
  }
}
