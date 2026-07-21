package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Vector2Test {

  private static final double DELTA = 1e-9;

  @Test
  void defaultConstructorCreatesNullVector() {
    Vector2 v = new Vector2();

    assertEquals(0.0, v.getX(), DELTA);
    assertEquals(0.0, v.getY(), DELTA);
  }

  @Test
  void copyConstructorCopiesCoordinates() {
    Vector2 original = new Vector2(3.0, 4.0);
    Vector2 copy = new Vector2(original);

    assertEquals(original, copy);
  }

  @Test
  void lengthReturnsEuclideanMagnitude() {
    Vector2 v = new Vector2(3.0, 4.0);

    assertEquals(5.0, v.length(), DELTA);
    assertEquals(25.0, v.lengthSq(), DELTA);
  }

  @Test
  void distanceReturnsEuclideanDistanceBetweenVectors() {
    Vector2 a = new Vector2(0.0, 0.0);
    Vector2 b = new Vector2(3.0, 4.0);

    assertEquals(5.0, a.distance(b), DELTA);
    assertEquals(25.0, a.distanceSq(b), DELTA);
  }

  @Test
  void angleReturnsDegreesFromPositiveXAxis() {
    assertEquals(0.0, new Vector2(1.0, 0.0).angle(), DELTA);
    assertEquals(90.0, new Vector2(0.0, 1.0).angle(), DELTA);
    assertEquals(180.0, new Vector2(-1.0, 0.0).angle(), DELTA);
    assertEquals(-90.0, new Vector2(0.0, -1.0).angle(), DELTA);
  }

  @Test
  void unitVectorHasLengthOneInTheSameDirection() {
    Vector2 unit = new Vector2(3.0, 4.0).unitVector();

    assertEquals(0.6, unit.getX(), DELTA);
    assertEquals(0.8, unit.getY(), DELTA);
  }

  @Test
  void unitVectorOfZeroVectorIsZeroVector() {
    Vector2 unit = new Vector2(0.0, 0.0).unitVector();

    assertEquals(0.0, unit.getX(), DELTA);
    assertEquals(0.0, unit.getY(), DELTA);
  }

  @Test
  void normalVectorIsPerpendicular() {
    Vector2 normal = new Vector2(3.0, 4.0).normalVector();

    assertEquals(-4.0, normal.getX(), DELTA);
    assertEquals(3.0, normal.getY(), DELTA);
    assertEquals(0.0, normal.dot(new Vector2(3.0, 4.0)), DELTA);
  }

  @Test
  void addSumsComponentwise() {
    Vector2 sum = new Vector2(1.0, 2.0).add(new Vector2(3.0, 4.0));

    assertEquals(new Vector2(4.0, 6.0), sum);
  }

  @Test
  void subSubtractsComponentwise() {
    Vector2 diff = new Vector2(5.0, 7.0).sub(new Vector2(2.0, 3.0));

    assertEquals(new Vector2(3.0, 4.0), diff);
  }

  @Test
  void multiplyScalesBothComponents() {
    Vector2 scaled = new Vector2(2.0, -3.0).multiply(2.0);

    assertEquals(new Vector2(4.0, -6.0), scaled);
  }

  @Test
  void dotComputesDotProduct() {
    double dot = new Vector2(1.0, 2.0).dot(new Vector2(3.0, 4.0));

    assertEquals(11.0, dot, DELTA);
  }

  @Test
  void rotateBy90DegreesRotatesCounterclockwise() {
    Vector2 rotated = new Vector2(1.0, 0.0).rotateBy(90.0);

    assertEquals(0.0, rotated.getX(), DELTA);
    assertEquals(1.0, rotated.getY(), DELTA);
  }

  @Test
  void rotateByPreservesLength() {
    Vector2 v = new Vector2(3.0, 4.0);
    Vector2 rotated = v.rotateBy(37.0);

    assertEquals(v.length(), rotated.length(), 1e-9);
  }

  @Test
  void rotateToPointsAtTheGivenAngleWithSameLength() {
    Vector2 v = new Vector2(5.0, 0.0);
    Vector2 rotated = v.rotateTo(90.0);

    assertEquals(0.0, rotated.getX(), 1e-9);
    assertEquals(5.0, rotated.getY(), 1e-9);
  }

  @Test
  void fromPolarBuildsVectorFromMagnitudeAndAngle() {
    Vector2 v = Vector2.fromPolar(10.0, 0.0);

    assertEquals(10.0, v.getX(), 1e-9);
    assertEquals(0.0, v.getY(), 1e-9);

    Vector2 up = Vector2.fromPolar(10.0, 90.0);
    assertEquals(0.0, up.getX(), 1e-9);
    assertEquals(10.0, up.getY(), 1e-9);
  }

  @Test
  void reverseNegatesBothComponents() {
    Vector2 reversed = new Vector2(3.0, -4.0).reverse();

    assertEquals(new Vector2(-3.0, 4.0), reversed);
  }

  @Test
  void equalsComparesByValue() {
    assertTrue(new Vector2(1.0, 2.0).equals(new Vector2(1.0, 2.0)));
    assertFalse(new Vector2(1.0, 2.0).equals(new Vector2(1.0, 3.0)));
    assertFalse(new Vector2(1.0, 2.0).equals("not a vector"));
  }

  @Test
  void equalVectorsHaveEqualHashCodes() {
    Vector2 a = new Vector2(1.0, 2.0);
    Vector2 b = new Vector2(1.0, 2.0);

    assertEquals(a.hashCode(), b.hashCode());
  }
}
