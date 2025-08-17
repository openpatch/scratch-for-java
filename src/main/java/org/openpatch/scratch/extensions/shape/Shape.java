package org.openpatch.scratch.extensions.shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import org.openpatch.scratch.extensions.math.Utils;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Represents a generic shape that can be transformed and drawn.
 * This class serves as a base for specific shapes like Rectangle, Circle, and
 * Ellipse.
 */
public class Shape {
  protected java.awt.Shape awtShape;

  protected Shape() {
  }

  private Shape(java.awt.Shape awtShape) {
    this.awtShape = awtShape;
  }

  /**
   * Check if the shape contains a point defined by its x and y coordinates.
   *
   * @param x the x-coordinate of the point
   * @param y the y-coordinate of the point
   *
   * @return true if the shape contains the point, false otherwise
   */
  public boolean contains(double x, double y) {
    return awtShape.contains(x, y);
  }

  /**
   * Check if the shape intersects with another shape.
   *
   * @param other the other shape to check intersection with
   *
   * @return true if the shapes intersect, false otherwise
   */
  public boolean intersects(Shape other) {
    Area a1 = new Area(awtShape);
    a1.intersect(new Area(other.awtShape));
    return !a1.isEmpty();
  }

  /**
   * Check if the shape is empty.
   *
   * @return true if the shape is empty, false otherwise
   */
  public Shape scale(double scaleX, double scaleY) {
    AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
    java.awt.Shape scaledShape = at.createTransformedShape(awtShape);
    return new Shape(scaledShape);
  }

  /**
   * Translate the shape by the specified distances in the x and y directions.
   *
   * @param dx the distance to translate in the x direction
   * @param dy the distance to translate in the y direction
   *
   * @return a new Shape object that is translated
   */
  public Shape translate(double dx, double dy) {
    AffineTransform at = AffineTransform.getTranslateInstance(dx, dy);
    java.awt.Shape translatedShape = at.createTransformedShape(awtShape);
    return new Shape(translatedShape);
  }

  /**
   * Rotate the shape by the specified angle around a given anchor point.
   *
   * @param theta   the angle in degrees to rotate
   * @param anchorX the x-coordinate of the anchor point
   * @param anchorY the y-coordinate of the anchor point
   *
   * @return a new Shape object that is rotated
   */
  public Shape rotate(double theta, double anchorX, double anchorY) {
    AffineTransform at = AffineTransform.getRotateInstance(Utils.degreesToRadians(theta), anchorX, anchorY);
    java.awt.Shape rotatedShape = at.createTransformedShape(awtShape);
    return new Shape(rotatedShape);
  }

  /**
   * Get the bounds of the shape.
   *
   * @return a Bounds object representing the bounding box of the shape
   */
  public Bounds getBounds() {
    java.awt.geom.Rectangle2D bounds = awtShape.getBounds2D();
    return new Bounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
  }

  public void draw(PGraphics buffer) {
    PathIterator path = this.awtShape.getPathIterator(null, 1);
    final float coord[] = new float[6];
    boolean began = false;
    while (!path.isDone()) {
      final int code = path.currentSegment(coord);
      if (code == PathIterator.SEG_MOVETO) {
        buffer.beginShape();
        began = true;
        buffer.vertex(coord[0], coord[1]);
      } else if (code == PathIterator.SEG_LINETO) {
        buffer.vertex(coord[0], coord[1]);
      } else if (code == PathIterator.SEG_CLOSE) {
        buffer.endShape(PConstants.CLOSE);
        began = false;
      } else if (code == PathIterator.SEG_CUBICTO) {
        buffer.vertex(coord[0], coord[1]);
        buffer.bezierVertex(coord[0], coord[1], coord[2], coord[3], coord[4], coord[5]);
      } else if (code == PathIterator.SEG_QUADTO) {
        buffer.quadraticVertex(coord[0], coord[1], coord[2], coord[3]);
      }
      path.next();
    }
    if (began) {
      buffer.endShape();
    }
  }
}
