package org.openpatch.scratch.extensions.shape;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a polygon shape defined by its vertices.
 */
public class Polygon extends Shape {

  private final List<Point2D.Double> points = new ArrayList<>();

  /**
   * Creates an empty polygon.
   */
  public Polygon() {
    this.awtShape = new Path2D.Double();
  }

  /**
   * Creates a polygon with the specified vertices.
   *
   * @param xPoints an array of x-coordinates
   * @param yPoints an array of y-coordinates
   */
  public Polygon(double[] xPoints, double[] yPoints) {
    if (xPoints.length != yPoints.length) {
      throw new IllegalArgumentException("xPoints and yPoints must have the same length");
    }
    this.awtShape = new Path2D.Double();
    for (int i = 0; i < xPoints.length; i++) {
      addPoint(xPoints[i], yPoints[i]);
    }
  }

  /**
   * Adds a point to the polygon.
   *
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  public void addPoint(double x, double y) {
    points.add(new Point2D.Double(x, y));
    rebuildPath();
  }

  /**
   * Rebuilds the Path2D from the stored points.
   */
  private void rebuildPath() {
    Path2D.Double path = new Path2D.Double();
    if (!points.isEmpty()) {
      Point2D.Double first = points.get(0);
      path.moveTo(first.x, first.y);
      for (int i = 1; i < points.size(); i++) {
        Point2D.Double p = points.get(i);
        path.lineTo(p.x, p.y);
      }
      path.closePath();
    }
    this.awtShape = path;
  }
}
