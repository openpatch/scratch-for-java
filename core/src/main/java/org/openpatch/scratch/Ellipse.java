package org.openpatch.scratch;

import org.openpatch.scratch.internal.geom.Geometry;

/** An ellipse inside a rectangle along the axes. */
public class Ellipse extends Shape {

  /**
   * @param x the smallest x of the rectangle around it
   * @param y the smallest y of the rectangle around it
   * @param width the width
   * @param height the height
   */
  public Ellipse(double x, double y, double width, double height) {
    this.setPoints(Geometry.ellipse(x + width / 2, y + height / 2, width / 2, height / 2));
  }
}
