package org.openpatch.scratch;

import java.util.Arrays;

/** A polygon given by its corners, concave ones included. The outline closes by itself. */
public class Polygon extends Shape {

  private double[] corners = new double[0];

  /** A polygon without corners yet; add them with {@link #addPoint(double, double)}. */
  public Polygon() {}

  /**
   * @param xPoints x of every corner
   * @param yPoints y of every corner, in the same order
   */
  public Polygon(double[] xPoints, double[] yPoints) {
    if (xPoints.length != yPoints.length) {
      throw new IllegalArgumentException("xPoints and yPoints must have the same length");
    }
    this.corners = new double[2 * xPoints.length];
    for (int i = 0; i < xPoints.length; i++) {
      this.corners[2 * i] = xPoints[i];
      this.corners[2 * i + 1] = yPoints[i];
    }
    this.setPoints(this.corners);
  }

  /**
   * Adds a corner after the last one.
   *
   * @param x x of the corner
   * @param y y of the corner
   *
   * @example.files PolygonAddPoint.java
   */
  public void addPoint(double x, double y) {
    this.corners = Arrays.copyOf(this.corners, this.corners.length + 2);
    this.corners[this.corners.length - 2] = x;
    this.corners[this.corners.length - 1] = y;
    this.setPoints(this.corners);
  }
}
