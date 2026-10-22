package org.openpatch.scratch;

import org.openpatch.scratch.internal.geom.Geometry;

/** A circle. */
public class Circle extends Shape {

  /**
   * @param x x of the centre
   * @param y y of the centre
   * @param radius the radius
   */
  public Circle(double x, double y, double radius) {
    this.setPoints(Geometry.ellipse(x, y, radius, radius));
  }
}
