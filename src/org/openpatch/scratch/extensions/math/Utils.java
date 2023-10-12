package org.openpatch.scratch.extensions.math;

public abstract class Utils {
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

  public static double degreesToRadians(double degrees) {
    return degrees * Math.PI / 180.0;
  }

  public static double radiansToDegrees(double radians) {
    return radians * 180.0 / Math.PI;
  }
}
