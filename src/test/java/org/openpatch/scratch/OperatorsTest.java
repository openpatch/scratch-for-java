package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OperatorsTest {

  private static final double DELTA = 1e-9;

  @Test
  void lerpInterpolatesBetweenTwoValues() {
    assertEquals(0.0, Operators.lerp(0.0, 10.0, 0.0), DELTA);
    assertEquals(5.0, Operators.lerp(0.0, 10.0, 0.5), DELTA);
    assertEquals(10.0, Operators.lerp(0.0, 10.0, 1.0), DELTA);
  }

  @Test
  void constrainClampsDoubleWithinBounds() {
    assertEquals(5.0, Operators.constrain(5.0, 0.0, 10.0), DELTA);
    assertEquals(0.0, Operators.constrain(-5.0, 0.0, 10.0), DELTA);
    assertEquals(10.0, Operators.constrain(15.0, 0.0, 10.0), DELTA);
  }

  @Test
  void constrainClampsIntWithinBounds() {
    assertEquals(5, Operators.constrain(5, 0, 10));
    assertEquals(0, Operators.constrain(-5, 0, 10));
    assertEquals(10, Operators.constrain(15, 0, 10));
  }

  @Test
  void minReturnsSmallestDouble() {
    assertEquals(-2.0, Operators.min(5.0, -2.0, 10.0), DELTA);
  }

  @Test
  void maxReturnsLargestDouble() {
    assertEquals(10.0, Operators.max(5.0, -2.0, 10.0), DELTA);
  }

  @Test
  void maxReturnsLargestDoubleWhenAllValuesAreNegative() {
    // Regression guard: Operators.max(double...) seeds its accumulator with
    // Double.MIN_VALUE (the smallest positive double), not
    // Double.NEGATIVE_INFINITY, so an all-negative input must still resolve
    // to the true maximum instead of the sentinel.
    assertEquals(-2.0, Operators.max(-5.0, -2.0, -10.0), DELTA);
  }

  @Test
  void minReturnsSmallestInt() {
    assertEquals(-2, Operators.min(5, -2, 10));
  }

  @Test
  void maxReturnsLargestInt() {
    assertEquals(10, Operators.max(5, -2, 10));
  }

  @Test
  void maxReturnsLargestIntWhenAllValuesAreNegative() {
    assertEquals(-2, Operators.max(-5, -2, -10));
  }

  @Test
  void mapRemapsValueToNewRange() {
    assertEquals(100.0, Operators.map(50.0, 0.0, 100.0, 0.0, 200.0), DELTA);
    assertEquals(-5.0, Operators.map(25.0, 0.0, 100.0, -10.0, 10.0), DELTA);
  }

  @Test
  void mapDoesNotClampOutOfRangeValues() {
    assertEquals(220.0, Operators.map(110.0, 0.0, 100.0, 0.0, 200.0), DELTA);
  }

  @Test
  void roundRoundsHalfUp() {
    assertEquals(2, Operators.round(2.4));
    assertEquals(3, Operators.round(2.5));
    assertEquals(3, Operators.round(2.6));
  }

  @Test
  void roundWithPrecisionRoundsToDecimalPlaces() {
    assertEquals(3.14, Operators.round(3.14159, 2), DELTA);
    assertEquals(3.1, Operators.round(3.14159, 1), DELTA);
  }

  @Test
  void modReturnsRemainderForDoubles() {
    assertEquals(1.0, Operators.mod(10.0, 3.0), DELTA);
    assertEquals(-1.0, Operators.mod(-10.0, 3.0), DELTA);
  }

  @Test
  void modReturnsRemainderForInts() {
    assertEquals(1, Operators.mod(10, 3));
    assertEquals(-1, Operators.mod(-7, 3));
  }

  @Test
  void absOfReturnsAbsoluteValueForInt() {
    assertEquals(5, Operators.absOf(-5));
    assertEquals(5, Operators.absOf(5));
  }

  @Test
  void absOfReturnsAbsoluteValueForDouble() {
    assertEquals(5.5, Operators.absOf(-5.5), DELTA);
  }

  @Test
  void floorOfRoundsDownToNearestWholeNumber() {
    assertEquals(1.0, Operators.floorOf(1.73), DELTA);
    assertEquals(-3.0, Operators.floorOf(-2.74), DELTA);
  }

  @Test
  void ceilingOfRoundsUpToNearestWholeNumber() {
    assertEquals(4.0, Operators.ceilingOf(3.14), DELTA);
    assertEquals(8.0, Operators.ceilingOf(7.68), DELTA);
  }

  @Test
  void sqrtOfReturnsSquareRoot() {
    assertEquals(2.0, Operators.sqrtOf(4.0), DELTA);
    assertEquals(1.4142135623730951, Operators.sqrtOf(2.0), DELTA);
  }

  @Test
  void trigFunctionsTakeDegrees() {
    assertEquals(0.0, Operators.sinOf(0.0), DELTA);
    assertEquals(1.0, Operators.sinOf(90.0), DELTA);
    assertEquals(1.0, Operators.cosOf(0.0), DELTA);
    assertEquals(-1.0, Operators.cosOf(180.0), DELTA);
    assertEquals(1.0, Operators.tanOf(45.0), 1e-8);
  }

  @Test
  void inverseTrigFunctionsReturnDegrees() {
    // Operators.{asinOf,acosOf,atanOf} are documented to mirror sinOf/cosOf/tanOf
    // and return an angle, so they should be the degree-valued inverse of those
    // functions: asinOf(sinOf(x)) == x, etc.
    assertEquals(90.0, Operators.asinOf(1.0), 1e-6);
    assertEquals(30.0, Operators.asinOf(0.5), 1e-6);
    assertEquals(0.0, Operators.acosOf(1.0), 1e-6);
    assertEquals(60.0, Operators.acosOf(0.5), 1e-6);
    assertEquals(45.0, Operators.atanOf(1.0), 1e-6);
  }

  @Test
  void lnOfReturnsNaturalLogarithm() {
    assertEquals(0.0, Operators.lnOf(1.0), DELTA);
    assertEquals(1.0, Operators.lnOf(Math.E), DELTA);
  }

  @Test
  void logOfReturnsBase10Logarithm() {
    assertEquals(2.0, Operators.logOf(100.0), DELTA);
  }

  @Test
  void eToThePowerOfReturnsExponential() {
    assertEquals(1.0, Operators.eToThePowerOf(0.0), DELTA);
    assertEquals(Math.E, Operators.eToThePowerOf(1.0), DELTA);
  }

  @Test
  void tenToThePowerOfReturnsPowerOfTen() {
    assertEquals(1.0, Operators.tenToThePowerOf(0.0), DELTA);
    assertEquals(1000000.0, Operators.tenToThePowerOf(6.0), DELTA);
  }
}
