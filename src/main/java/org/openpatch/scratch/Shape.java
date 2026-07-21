package org.openpatch.scratch;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import org.openpatch.scratch.internal.Utils;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Represents a generic shape that can be transformed and drawn.
 * This class serves as a base for specific shapes like Rectangle, Circle, and
 * Ellipse.
 */
public class Shape {
  protected java.awt.Shape awtShape;

  /**
   * The outline flattened to points, and whether it came out convex - worked out
   * once and reused, so that {@link #intersects(Shape)} can take the cheap route
   * without re-walking the path every call. Both are derived from
   * {@link #awtShape} and must be dropped whenever it is replaced.
   */
  private double[] cachedPoints;
  private boolean cachedConvex;
  private Bounds cachedBounds;

  /**
   * Above this many corners the separating-axis test stops being cheaper than
   * the general {@link Area} one, so shapes that flatten to a lot of segments
   * (circles, ellipses) keep using the general path.
   */
  private static final int MAX_SAT_POINTS = 8;

  protected Shape() {
  }

  private Shape(java.awt.Shape awtShape) {
    this.awtShape = awtShape;
  }

  /** Call after replacing {@link #awtShape} so the derived data is rebuilt. */
  protected void invalidateCache() {
    this.cachedPoints = null;
    this.cachedBounds = null;
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
    // Sprite hitboxes are almost always convex quads, and building two Areas to
    // compare two quads costs more than the rest of a frame's collision work put
    // together. Separating axes give the same answer for the convex case.
    double[] a = this.points();
    double[] b = other.points();
    if (a != null && b != null && this.cachedConvex && other.cachedConvex) {
      return !separated(a, b) && !separated(b, a);
    }

    Area a1 = new Area(awtShape);
    a1.intersect(new Area(other.awtShape));
    return !a1.isEmpty();
  }

  /**
   * Checks whether some edge of {@code a} has all of {@code b} on its far side,
   * which for convex outlines means the two cannot be touching.
   */
  private static boolean separated(double[] a, double[] b) {
    for (int i = 0; i < a.length; i += 2) {
      int j = (i + 2) % a.length;
      // Outward normal of the edge from point i to point j.
      double axisX = a[j + 1] - a[i + 1];
      double axisY = a[i] - a[j];

      double aMin = Double.POSITIVE_INFINITY;
      double aMax = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < a.length; k += 2) {
        double p = a[k] * axisX + a[k + 1] * axisY;
        if (p < aMin)
          aMin = p;
        if (p > aMax)
          aMax = p;
      }

      double bMin = Double.POSITIVE_INFINITY;
      double bMax = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < b.length; k += 2) {
        double p = b[k] * axisX + b[k + 1] * axisY;
        if (p < bMin)
          bMin = p;
        if (p > bMax)
          bMax = p;
      }

      // Not "<", so that outlines meeting exactly along an edge count as apart,
      // the same way the Area path does - their overlap has no area.
      if (aMax <= bMin || bMax <= aMin) {
        return true;
      }
    }
    return false;
  }

  /**
   * The outline as flat {@code x, y, x, y, ...} pairs, or null if the shape is
   * not a single small convex ring that {@link #separated} can handle.
   */
  private double[] points() {
    if (this.cachedPoints != null) {
      return this.cachedPoints;
    }
    PathIterator it = this.awtShape.getPathIterator(null, 1);
    double[] coord = new double[6];
    double[] buffer = new double[2 * MAX_SAT_POINTS];
    int n = 0;
    boolean seenMoveTo = false;
    while (!it.isDone()) {
      int code = it.currentSegment(coord);
      if (code == PathIterator.SEG_CLOSE) {
        it.next();
        continue;
      }
      if (code == PathIterator.SEG_MOVETO) {
        if (seenMoveTo) {
          return null; // more than one ring
        }
        seenMoveTo = true;
      } else if (code != PathIterator.SEG_LINETO) {
        return null; // a curve survived flattening
      }
      if (n + 2 > buffer.length) {
        return null; // too many corners to be worth the separating-axis test
      }
      buffer[n++] = coord[0];
      buffer[n++] = coord[1];
      it.next();
    }
    if (n < 6) {
      return null; // fewer than three corners is not an area
    }
    // A flattened ring often repeats its first point at the end; drop it so the
    // edge walk does not produce a zero-length axis.
    if (n >= 8 && buffer[0] == buffer[n - 2] && buffer[1] == buffer[n - 1]) {
      n -= 2;
    }

    double[] points = java.util.Arrays.copyOf(buffer, n);
    this.cachedConvex = isConvex(points);
    this.cachedPoints = points;
    return points;
  }

  /** True if every turn along the ring goes the same way. */
  private static boolean isConvex(double[] p) {
    int sign = 0;
    for (int i = 0; i < p.length; i += 2) {
      int j = (i + 2) % p.length;
      int k = (i + 4) % p.length;
      double cross = (p[j] - p[i]) * (p[k + 1] - p[j + 1]) - (p[j + 1] - p[i + 1]) * (p[k] - p[j]);
      if (cross == 0) {
        continue;
      }
      int s = cross > 0 ? 1 : -1;
      if (sign == 0) {
        sign = s;
      } else if (sign != s) {
        return false;
      }
    }
    return true;
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
    if (this.cachedBounds == null) {
      java.awt.geom.Rectangle2D bounds = awtShape.getBounds2D();
      this.cachedBounds = new Bounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    return this.cachedBounds;
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
