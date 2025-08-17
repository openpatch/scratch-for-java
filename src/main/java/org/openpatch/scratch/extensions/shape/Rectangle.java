package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Rectangle2D;

/**
 * Represents a rectangle shape defined by its position and size.
 */
public class Rectangle extends Shape {
  /**
   * Creates a rectangle with the specified position and size.
   *
   * @param x      the x-coordinate of the rectangle's top-left corner
   * @param y      the y-coordinate of the rectangle's top-left corner
   * @param width  the width of the rectangle
   * @param height the height of the rectangle
   */
  public Rectangle(double x, double y, double width, double height) {
    this.awtShape = new Rectangle2D.Double(x, y, width, height);
  }
}
