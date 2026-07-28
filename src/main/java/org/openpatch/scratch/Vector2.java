package org.openpatch.scratch;


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
 *
 * @example.files Vector2Constructors.java
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
   *
   * @example.files Vector2FromPolar.java
   */
  public static Vector2 fromPolar(double magnitude, double angle) {
    return new Vector2(magnitude * Operators.cosOf(angle), magnitude * Operators.sinOf(angle));
  }

  /**
   * Calculates the length of the vector.
   *
   * @return the length of the vector
   *
   * @example.files Vector2Length.java
   */
  public double length() {
    return Operators.sqrtOf(lengthSq());
  }

  /**
   * Calculates the squared length of the vector.
   *
   * @return the squared length of the vector
   *
   * @example.files Vector2LengthSq.java
   */
  public double lengthSq() {
    return x * x + y * y;
  }

  /**
   * Calculates the squared distance to another vector.
   *
   * @param v a vector
   * @return the squared distance
   *
   * @example.files Vector2DistanceSq.java
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
   *
   * @example.files Vector2Distance.java
   */
  public double distance(Vector2 v) {
    return Operators.sqrtOf(distanceSq(v));
  }

  /**
   * Returns the angle of the vector.
   *
   * @return the angle
   *
   * @example.files Vector2Angle.java
   */
  public double angle() {
    return Math.atan2(y, x) * 180 / Math.PI;
  }

  /**
   * Returns the unit vector.
   *
   * @return a unit vector
   *
   * @example.files Vector2UnitVector.java
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
   *
   * @example.files Vector2NormalVector.java
   */
  public Vector2 normalVector() {
    return new Vector2(-y, x);
  }

  /**
   * Adds another vector and returns the result
   *
   * @return the sum
   *
   * @example.files Vector2Add.java
   */
  public Vector2 add(Vector2 v) {
    return new Vector2(x + v.x, y + v.y);
  }

  /**
   * Substracts another vector and returns the result
   *
   * @return the difference
   *
   * @example.files Vector2Sub.java
   */
  public Vector2 sub(Vector2 v) {
    return new Vector2(x - v.x, y - v.y);
  }

  /**
   * Multiplies a scalar and return the result.
   *
   * @param scalar a scalar
   * @return the vector
   *
   * @example.files Vector2Multiply.java
   */
  public Vector2 multiply(double scalar) {
    return new Vector2(x * scalar, y * scalar);
  }

  /**
   * Calculates the dot product.
   *
   * @param v a vector
   * @return the dot product
   *
   * @example.files Vector2Dot.java
   */
  public double dot(Vector2 v) {
    return x * v.x + y * v.y;
  }

  /**
   * Rotates the vector by an angle and returns the result.
   *
   * @param angle an angle between [0, ..., 360]
   * @return the rotated vector
   *
   * @example.files Vector2RotateBy.java
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
   *
   * @example.files Vector2RotateTo.java
   */
  public Vector2 rotateTo(double angle) {
    return fromPolar(length(), angle);
  }

  /**
   * Reverses a vector and returns the result.
   *
   * @return the reversed vector
   *
   * @example.files Vector2Reverse.java
   */
  public Vector2 reverse() {
    return new Vector2(-x, -y);
  }

  /**
   * Returns the x coordinate of the vector
   *
   * @return the x coordinate
   *
   * @example.files Vector2GetX.java
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y coordinate of the vector
   *
   * @return the y coordinate
   *
   * @example.files Vector2GetY.java
   */
  public double getY() {
    return y;
  }

  /**
   * Creates a clone of the vector
   *
   * @return the cloned vector
   *
   * @example.files Vector2Clone.java
   */
  @Override
  public Vector2 clone() {
    return new Vector2(x, y);
  }

  /**
   * Compares this vector with another object.
   *
   * @param obj the object to compare with
   * @return true if the other object is a vector with the same x- and y-coordinate
   *
   * @example.files Vector2Equals.java
   */
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

  /**
   * Returns the hash code of the vector. Vectors that are equal have the same hash code, which is
   * what lets them be used as keys in a {@code HashMap}.
   *
   * @return the hash code of the vector
   *
   * @example.files Vector2HashCode.java
   */
  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  /**
   * Returns the vector as a text, for example {@code Vector2[60.0, 80.0]}.
   *
   * @return the vector as a text
   *
   * @example.files Vector2ToString.java
   */
  @Override
  public String toString() {
    return "Vector2[" + x + ", " + y + "]";
  }
}
