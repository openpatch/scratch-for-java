package org.openpatch.scratch;

/** A triangle. */
public class Triangle extends Shape {

  /**
   * @param x1 x of the first corner
   * @param y1 y of the first corner
   * @param x2 x of the second corner
   * @param y2 y of the second corner
   * @param x3 x of the third corner
   * @param y3 y of the third corner
   */
  public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
    this.setPoints(new double[] {x1, y1, x2, y2, x3, y3});
  }
}
