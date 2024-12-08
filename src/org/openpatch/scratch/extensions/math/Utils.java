package org.openpatch.scratch.extensions.math;

/**
 * The Utils class provides utility methods for mathematical operations.
 * It includes methods for rotating points around a specified origin and
 * converting angles between degrees and radians.
 */
public abstract class Utils {

  private Utils() {}
  /**
   * Rotates a point (x, y) around a specified origin (originX, originY) by a given angle in degrees.
   *
   * @param x the x-coordinate of the point to be rotated
   * @param y the y-coordinate of the point to be rotated
   * @param originX the x-coordinate of the origin point around which to rotate
   * @param originY the y-coordinate of the origin point around which to rotate
   * @param degrees the angle in degrees by which to rotate the point
   * @return a double array containing the rotated x and y coordinates
   */
  public static double[] rotateXY(
      double x, double y, double originX, double originY, double degrees) {
    double[] rotatedXY = new double[2];

    double radians = degrees * Math.PI / 180.0;
    x = x - originX;
    y = y - originY;
    rotatedXY[0] = (x * Math.cos(radians) - y * Math.sin(radians)) + originX;
    rotatedXY[1] = (x * Math.sin(radians) + y * Math.cos(radians)) + originY;

    return rotatedXY;
  }

  /**
   * Converts an angle measured in degrees to an approximately equivalent angle measured in radians.
   *
   * @param degrees the angle in degrees
   * @return the angle in radians
   */
  public static double degreesToRadians(double degrees) {
    return degrees * Math.PI / 180.0;
  }

  /**
   * Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
   *
   * @param radians the angle in radians
   * @return the angle in degrees
   */
  public static double radiansToDegrees(double radians) {
    return radians * 180.0 / Math.PI;
  }
}
