package org.openpatch.scratch;

import org.openpatch.scratch.internal.geom.Geometry;

/**
 * A shape on the stage: a polygon, rectangle, circle, ellipse or triangle. Shapes serve as
 * hitboxes ({@code sprite.setHitbox(...)}) and can be tested against each other. Circles and
 * ellipses are stored as polygons that stay within a quarter pixel of the true outline.
 *
 * <p>Shapes that only touch, along an edge or at a corner, do not intersect.
 */
public class Shape {

  private double[] ring = new double[0];
  private Bounds bounds;
  private double[][] pieces;
  private Boolean convex;

  /** An empty shape, for subclasses that set their points. */
  protected Shape() {}

  private Shape(double[] ring) {
    this.ring = ring;
  }

  /**
   * A shape from the points of its outline.
   *
   * @param points {@code x0, y0, x1, y1, …}; the outline is closed
   * @return the shape
   */
  static Shape of(double[] points) {
    return new Shape(points.clone());
  }

  /**
   * Sets the outline; subclasses call it from their constructors.
   *
   * @param points {@code x0, y0, x1, y1, …}; the outline is closed
   */
  protected void setPoints(double[] points) {
    if (points.length % 2 != 0) {
      throw new IllegalArgumentException("A shape needs pairs of x and y.");
    }
    this.ring = points.clone();
    this.bounds = null;
    this.pieces = null;
    this.convex = null;
  }

  /** @return the outline's points {@code x0, y0, x1, y1, …} (a copy) */
  public double[] getPoints() {
    return this.ring.clone();
  }

  /**
   * @param x x of a point
   * @param y y of a point
   * @return whether the point lies inside the shape
   *
   * @example.files ShapeContains.java
   */
  public boolean contains(double x, double y) {
    return this.ring.length >= 6 && Geometry.contains(this.ring, x, y);
  }

  /**
   * @param other another shape
   * @return whether the two shapes overlap with some area; shapes that only touch do not
   *
   * @example.files ShapeIntersects.java
   */
  public boolean intersects(Shape other) {
    if (this.ring.length < 6 || other.ring.length < 6) {
      return false;
    }
    Bounds a = this.getBounds(), b = other.getBounds();
    if (!a.intersects(b)) {
      return false;
    }
    if (this.isConvex() && other.isConvex()) {
      return Geometry.convexOverlap(this.ring, other.ring);
    }
    return Geometry.overlap(this.pieces(), other.pieces());
  }

  private boolean isConvex() {
    if (this.convex == null) {
      this.convex = Geometry.isConvex(this.ring);
    }
    return this.convex;
  }

  private double[][] pieces() {
    if (this.pieces == null) {
      this.pieces = Geometry.convexPieces(this.ring);
    }
    return this.pieces;
  }

  /**
   * @param scaleX factor along x (negative mirrors)
   * @param scaleY factor along y
   * @return the shape scaled about the origin
   *
   * @example.files ShapeScale.java
   */
  public Shape scale(double scaleX, double scaleY) {
    return new Shape(Geometry.transform(this.ring, scaleX, 0, 0, scaleY, 0, 0));
  }

  /**
   * @param dx distance along x
   * @param dy distance along y
   * @return the shape moved
   *
   * @example.files ShapeTranslate.java
   */
  public Shape translate(double dx, double dy) {
    return new Shape(Geometry.transform(this.ring, 1, 0, 0, 1, dx, dy));
  }

  /**
   * Turns the shape about a point, from the x axis towards the y axis (anticlockwise when y points
   * up).
   *
   * @param degrees the angle
   * @param anchorX x of the point to turn about
   * @param anchorY y of the point to turn about
   * @return the shape turned
   *
   * @example.files ShapeRotate.java
   */
  public Shape rotate(double degrees, double anchorX, double anchorY) {
    double r = Math.toRadians(degrees), cos = Math.cos(r), sin = Math.sin(r);
    return new Shape(Geometry.transform(this.ring, cos, sin, -sin, cos,
        anchorX - cos * anchorX + sin * anchorY, anchorY - sin * anchorX - cos * anchorY));
  }

  /**
   * @return the smallest rectangle around the shape
   *
   * @example.files ShapeGetBounds.java
   */
  public Bounds getBounds() {
    if (this.bounds == null) {
      double[] b = Geometry.bounds(this.ring);
      this.bounds = new Bounds(b[0], b[1], b[2] - b[0], b[3] - b[1]);
    }
    return this.bounds;
  }
}
