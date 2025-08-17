package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Ellipse2D;

/**
 * Represents a circle shape defined by its center and radius.
 */
public class Circle extends Shape {
  /**
   * Creates a circle with no specified position or size.
   *
   * @param x      the x-coordinate of the circle's center
   * @param y      the y-coordinate of the circle's center
   * @param radius the radius of the circle
   */
  public Circle(double x, double y, double radius) {
    this.awtShape = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
  }
}
