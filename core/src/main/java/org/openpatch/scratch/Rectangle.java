package org.openpatch.scratch;

/** A rectangle along the axes. */
public class Rectangle extends Shape {

  /**
   * @param x the smallest x
   * @param y the smallest y
   * @param width the width
   * @param height the height
   */
  public Rectangle(double x, double y, double width, double height) {
    this.setPoints(new double[] {x, y, x + width, y, x + width, y + height, x, y + height});
  }
}
