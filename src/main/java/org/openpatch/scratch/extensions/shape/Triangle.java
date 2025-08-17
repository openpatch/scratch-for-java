package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Path2D;

/**
 * Represents a triangle shape defined by three vertices.
 */
public class Triangle extends Shape {
  /**
   * Creates a triangle with the specified vertices.
   *
   * @param x1 the x-coordinate of the first vertex
   * @param y1 the y-coordinate of the first vertex
   * @param x2 the x-coordinate of the second vertex
   * @param y2 the y-coordinate of the second vertex
   * @param x3 the x-coordinate of the third vertex
   * @param y3 the y-coordinate of the third vertex
   */
  public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
    var awtPath = new Path2D.Double();
    awtPath.moveTo(x1, y1);
    awtPath.lineTo(x2, y2);
    awtPath.lineTo(x3, y3);
    awtPath.closePath();

    this.awtShape = awtPath;
  }
}
