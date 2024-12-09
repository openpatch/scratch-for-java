package org.openpatch.scratch.extensions.math;

import org.openpatch.scratch.Operators;

/**
 * The {@code Vector2} class represents a 2D vector with x and y coordinates. It provides various
 * methods for vector operations such as addition, subtraction, scalar multiplication, dot product,
 * rotation, and more.
 *
 * <p>Instances of this class are immutable.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Vector2 v1 = new Vector2(3, 4);
 * Vector2 v2 = new Vector2(1, 2);
 * Vector2 sum = v1.add(v2);
 * double length = v1.length();
 * }</pre>
 */
public class Vector2 {
  private final double x;
  private final double y;

  /** Constructor for the null vector. */
  public Vector2() {
    this.x = 0;
    this.y = 0;
  }

  /**
   * Constructor for a 2D vector.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public Vector2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Copy constructor for copying a vector.
   *
   * @param v a vector
   */
  public Vector2(Vector2 v) {
    this.x = v.x;
    this.y = v.y;
  }

  /**
   * Constructor method for constructing a vector based on polar coordinates.
   *
   * @param magnitude of the vector
   * @param angle of the vector
   * @return a vector
   */
  public static Vector2 fromPolar(double magnitude, double angle) {
    return new Vector2(magnitude * Operators.cosOf(angle), magnitude * Operators.sinOf(angle));
  }

  /**
   * Calculates the length of the vector.
   *
   * @return the length of the vector
   */
  public double length() {
    return Operators.sqrtOf(lengthSq());
  }

  /**
   * Calculates the squared length of the vector.
   *
   * @return the squared length of the vector
   */
  public double lengthSq() {
    return x * x + y * y;
  }

  /**
   * Calculates the squared distance to another vector.
   *
   * @param v a vector
   * @return the squared distance
   */
  public double distanceSq(Vector2 v) {
    double dx = v.x - x;
    double dy = v.y - y;
    return dx * dx + dy * dy;
  }

  /**
   * Calculates the distance to another vector.
   *
   * @param v a vector
   * @return the distance
   */
  public double distance(Vector2 v) {
    return Operators.sqrtOf(distanceSq(v));
  }

  /**
   * Returns the angle of the vector.
   *
   * @return the angle
   */
  public double angle() {
    return Math.atan2(y, x) * 180 / Math.PI;
  }

  /**
   * Returns the unit vector.
   *
   * @return a unit vector
   */
  public Vector2 unitVector() {
    double mag = length();
    if (mag > 0) {
      return new Vector2(x / mag, y / mag);
    } else {
      return new Vector2(0, 0);
    }
  }

  /**
   * Returns the normal vector, which is perpendicular to the vector.
   *
   * @return a normal vector
   */
  public Vector2 normalVector() {
    return new Vector2(-y, x);
  }

  /**
   * Adds another vector and returns the result
   *
   * @return the sum
   */
  public Vector2 add(Vector2 v) {
    return new Vector2(x + v.x, y + v.y);
  }

  /**
   * Substracts another vector and returns the result
   *
   * @return the difference
   */
  public Vector2 sub(Vector2 v) {
    return new Vector2(x - v.x, y - v.y);
  }

  /**
   * Multiplies a scalar and return the result.
   *
   * @param scalar a scalar
   * @return the vector
   */
  public Vector2 multiply(double scalar) {
    return new Vector2(x * scalar, y * scalar);
  }

  /**
   * Calculates the dot product.
   *
   * @param v a vector
   * @return the dot product
   */
  public double dot(Vector2 v) {
    return x * v.x + y * v.y;
  }

  /**
   * Rotates the vector by an angle and returns the result.
   *
   * @param angle an angle between [0, ..., 360]
   * @return the rotated vector
   */
  public Vector2 rotateBy(double angle) {
    angle = angle * Math.PI / 180;
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double rx = x * cos - y * sin;
    return new Vector2(rx, x * sin + y * cos);
  }

  /**
   * Rotates the vector to a given angle and returns the result.
   *
   * @param angle an angle between [0,...,360]
   * @return the rotated vector
   */
  public Vector2 rotateTo(double angle) {
    return fromPolar(length(), angle);
  }

  /**
   * Reverses a vector and returns the result.
   *
   * @return the reversed vector
   */
  public Vector2 reverse() {
    return new Vector2(-x, -y);
  }

  /**
   * Returns the x coordinate of the vector
   *
   * @return the x coordinate
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y coordinate of the vector
   *
   * @return the y coordinate
   */
  public double getY() {
    return y;
  }

  /**
   * Creates a clone of the vector
   *
   * @return the cloned vector
   */
  @Override
  public Vector2 clone() {
    return new Vector2(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Vector2) {
      Vector2 v = (Vector2) obj;
      return (x == v.x) && (y == v.y);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return "Vector2[" + x + ", " + y + "]";
  }
}
