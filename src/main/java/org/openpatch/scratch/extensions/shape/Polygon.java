package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Path2D;

/**
 * Represents a polygon shape defined by its vertices.
 */
public class Polygon extends Shape {

  /**
   * Creates a polygon with no points.
   */
  public Polygon() {
    this.awtShape = new Path2D.Double();
  }

  /**
   * Creates a polygon with the specified vertices.
   *
   * @param xPoints an array of x-coordinates of the polygon's vertices
   * @param yPoints an array of y-coordinates of the polygon's vertices
   */
  public Polygon(double[] xPoints, double[] yPoints) {
    if (xPoints.length != yPoints.length) {
      throw new IllegalArgumentException("xPoints and yPoints must have same length");
    }
    var awtPath = new Path2D.Double();
    awtPath.moveTo(xPoints[0], yPoints[0]);
    for (int i = 1; i < xPoints.length; i++) {
      awtPath.lineTo(xPoints[i], yPoints[i]);
    }
    awtPath.closePath();

    this.awtShape = awtPath;
  }

  /**
   * Adds a point to the polygon.
   *
   * @param x the x-coordinate of the point
   * @param y the y-coordinate of the point
   */
  public void addPoint(double x, double y) {
    if (this.awtShape instanceof Path2D path) {
      path.lineTo(x, y);
    } else {
      throw new IllegalStateException("Shape is not a Path2D");
    }
  }
}
