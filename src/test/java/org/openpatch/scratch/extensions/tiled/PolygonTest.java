package org.openpatch.scratch.extensions.tiled;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PolygonTest {

  private static final double DELTA = 1e-9;

  @Test
  void getPointsParsesSpaceSeparatedCommaPairs() {
    Polygon polygon = new Polygon();
    polygon.points = "0,0 10,0 10,10 0,10";

    double[][] points = polygon.getPoints();

    assertEquals(4, points.length);
    assertArrayEquals(new double[] {0, 0}, points[0], DELTA);
    assertArrayEquals(new double[] {10, 0}, points[1], DELTA);
    assertArrayEquals(new double[] {10, 10}, points[2], DELTA);
    assertArrayEquals(new double[] {0, 10}, points[3], DELTA);
  }

  @Test
  void getPointsHandlesNegativeAndDecimalCoordinates() {
    Polygon polygon = new Polygon();
    polygon.points = "-1.5,2.25 3,-4";

    double[][] points = polygon.getPoints();

    assertArrayEquals(new double[] {-1.5, 2.25}, points[0], DELTA);
    assertArrayEquals(new double[] {3, -4}, points[1], DELTA);
  }

  @Test
  void getPointsHandlesASinglePoint() {
    Polygon polygon = new Polygon();
    polygon.points = "5,5";

    double[][] points = polygon.getPoints();

    assertEquals(1, points.length);
    assertArrayEquals(new double[] {5, 5}, points[0], DELTA);
  }
}
