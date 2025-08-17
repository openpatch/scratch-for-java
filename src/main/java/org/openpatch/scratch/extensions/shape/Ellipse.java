package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Ellipse2D;

/**
 * Represents an ellipse shape.
 */
public class Ellipse extends Shape {
  /**
   * Creates an ellipse with no specified position or size.
   */
  public Ellipse(double x, double y, double width, double height) {
    this.awtShape = new Ellipse2D.Double(x, y, width, height);
  }
}
