package org.openpatch.scratch;

public class Operators {
  /**
   * Calculates a number between two numbers at a specific increment. The <b>amt</b> parameter is
   * the amount to interpolate between the two values where 0.0 equal to the first point, 0.1 is
   * very near the first point, 0.5 is half-way in between, etc. The lerp function is convenient for
   * creating motion along a straight path and for drawing dotted lines.
   *
   * @param start first value
   * @param stop second value
   * @param amt float between 0.0 and 1.0
   */
  public static final double lerp(double start, double stop, double amt) {
    return start + (stop - start) * amt;
  }

  /**
   * Calculates a number between two numbers at a specific increment. The <b>amt</b> parameter is
   * the amount to interpolate between the two values where 0.0 equal to the first point, 0.1 is
   * very near the first point, 0.5 is half-way in between, etc. The lerp function is convenient for
   * creating motion along a straight path and for drawing dotted lines.
   *
   * @param start first value
   * @param stop second value
   * @param amt float between 0.0 and 1.0
   */
  public static final float lerp(float start, float stop, float amt) {
    return start + (stop - start) * amt;
  }

  /**
   * Constrains a value to not exceed a maximum and minimum value.
   *
   * @param amt the value to constrain
   * @param low minimum limit
   * @param high maximum limit
   */
  public static final float constrain(float amt, float low, float high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
  }

  /**
   * Constrains a value to not exceed a maximum and minimum value.
   *
   * @param amt the value to constrain
   * @param low minimum limit
   * @param high maximum limit
   */
  public static final double constrain(double amt, double low, double high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
  }

  /**
   * Constrains a value to not exceed a maximum and minimum value.
   *
   * @param amt the value to constrain
   * @param low minimum limit
   * @param high maximum limit
   */
  public static final int constrain(int amt, int low, int high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
  }

  /**
   * Determines the smallest value in a sequence of numbers, and then returns that value.
   * <b>min()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the minimum
   */
  public static final double min(double... v) {
    double m = Double.MAX_VALUE;
    for (var vv : v) {
      if (m > vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Determines the largest value in a sequence of numbers, and then returns that value.
   * <b>max()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the maximum
   */
  public static final double max(double... v) {
    double m = Double.MIN_VALUE;
    for (var vv : v) {
      if (m < vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Determines the smallest value in a sequence of numbers, and then returns that value.
   * <b>min()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the minimum
   */
  public static final float min(float... v) {
    float m = Float.MAX_VALUE;
    for (var vv : v) {
      if (m > vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Determines the largest value in a sequence of numbers, and then returns that value.
   * <b>max()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the maximum
   */
  public static final float max(float... v) {
    float m = Float.MIN_VALUE;
    for (var vv : v) {
      if (m < vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Determines the smallest value in a sequence of numbers, and then returns that value.
   * <b>min()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the minimum
   */
  public static final int min(int... v) {
    int m = Integer.MAX_VALUE;
    for (var vv : v) {
      if (m > vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Determines the largest value in a sequence of numbers, and then returns that value.
   * <b>max()</b> accepts either two or three <b>float</b> or <b>int</b> values as parameters, or an
   * array of any length.
   *
   * @param v values
   * @return the maximum
   */
  public static final int max(int... v) {
    int m = Integer.MIN_VALUE;
    for (var vv : v) {
      if (m < vv) {
        m = vv;
      }
    }
    return m;
  }

  /**
   * Re-maps a number from one range to another.<br>
   * <br>
   * In the first example above, the number 25 is converted from a value in the range of 0 to 100
   * into a value that ranges from the left edge of the window (0) to the right edge (width).<br>
   * <br>
   * As shown in the second example, numbers outside the range are not clamped to the minimum and
   * maximum parameters values, because out-of-range values are often intentional and useful.
   *
   * @param value the incoming value to be converted
   * @param start1 lower bound of the value's current range
   * @param stop1 upper bound of the value's current range
   * @param start2 lower bound of the value's target range
   * @param stop2 upper bound of the value's target range
   */
  public static final float map(float value, float start1, float stop1, float start2, float stop2) {
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
  }

  /**
   * Re-maps a number from one range to another.<br>
   * <br>
   * In the first example above, the number 25 is converted from a value in the range of 0 to 100
   * into a value that ranges from the left edge of the window (0) to the right edge (width).<br>
   * <br>
   * As shown in the second example, numbers outside the range are not clamped to the minimum and
   * maximum parameters values, because out-of-range values are often intentional and useful.
   *
   * @param value the incoming value to be converted
   * @param start1 lower bound of the value's current range
   * @param stop1 upper bound of the value's current range
   * @param start2 lower bound of the value's target range
   * @param stop2 upper bound of the value's target range
   */
  public static final double map(
      double value, double start1, double stop1, double start2, double stop2) {
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
  }

  /**
   * The standard rules of rounding are followed; decimals that are .5 or higher are rounded up,
   * whereas decimals less than .5 are rounded down.
   *
   * @param x a value
   * @return the rounded value
   */
  public static final int round(double x) {
    return (int) Math.round(x);
  }

  /**
   * "mod" is an abbreviation for "modulo". Modulo returns the remainder when the first input is
   * divided by the second input.
   *
   * @param x first value
   * @param y second value
   * @return reminder of first value divided by second value
   */
  public static final float mod(float x, float y) {
    return x % y;
  }

  /**
   * "mod" is an abbreviation for "modulo". Modulo returns the remainder when the first input is
   * divided by the second input.
   *
   * @param x first value
   * @param y second value
   * @return reminder of first value divided by second value
   */
  public static final double mod(double x, double y) {
    return x % y;
  }

  /**
   * "mod" is an abbreviation for "modulo". Modulo returns the remainder when the first input is
   * divided by the second input.
   *
   * @param x first value
   * @param y second value
   * @return reminder of first value divided by second value
   */
  public static final int mod(int x, int y) {
    return x % y;
  }

  /**
   * "abs" is an abbreviation for "absolute value." The absolute value of a number is its distance
   * from 0. A simpler way to describe an absolute value is that it makes any number positive. If it
   * is negative, it becomes positive, and if it is positive, it stays the same.
   *
   * @param x a value
   * @return the absolute value
   */
  public static final int absOf(int x) {
    return Math.abs(x);
  }

  /**
   * "abs" is an abbreviation for "absolute value." The absolute value of a number is its distance
   * from 0. A simpler way to describe an absolute value is that it makes any number positive. If it
   * is negative, it becomes positive, and if it is positive, it stays the same.
   *
   * @param x a value
   * @return the absolute value
   */
  public static final double absOf(double x) {
    return Math.abs(x);
  }

  /**
   * "abs" is an abbreviation for "absolute value." The absolute value of a number is its distance
   * from 0. A simpler way to describe an absolute value is that it makes any number positive. If it
   * is negative, it becomes positive, and if it is positive, it stays the same.
   *
   * @param x a value
   * @return the absolute value
   */
  public static final float absOf(float x) {
    return Math.abs(x);
  }

  /**
   * This always rounds the number down to the greatest whole number less than or equal to the
   * number. For example, floor(1.73) = 1 and floor(-2.74) = -3.
   *
   * @param x a value
   * @return the floor of that value
   */
  public static final double floorOf(double x) {
    return Math.floor(x);
  }

  /**
   * This always rounds the number down to the greatest whole number less than or equal to the
   * number. For example, floor(1.73) = 1 and floor(-2.74) = -3.
   *
   * @param x a value
   * @return the floor of that value
   */
  public static final float floorOf(float x) {
    return (float) Math.floor(x);
  }

  /**
   * This always rounds the number up to the least whole number greater than or equal to the number.
   * For example, ceiling(3.14) = 4 and ceiling(7.68) = 8.
   *
   * @param x a value
   * @return the ceiling of that value
   */
  public static final double ceilingOf(double x) {
    return Math.ceil(x);
  }

  /**
   * This always rounds the number up to the least whole number greater than or equal to the number.
   * For example, ceiling(3.14) = 4 and ceiling(7.68) = 8.
   *
   * @param x a value
   * @return the ceiling of that value
   */
  public static final float ceilingOf(float x) {
    return (float) Math.ceil(x);
  }

  /**
   * "sqrt" is an abbreviation for "square root." A number that is squared is multiplied by itself.
   * For example, when 2 is squared, the answer is 2×2, which is 4. When a number is square rooted,
   * the answer is the number that was squared to get it. So, the square root of 4 is 2. Similarly,
   * the square root of 2 is about 1.414213562373095 because 1.4142135623730952
   * (1.414213562373095 × 1.414213562373095) is close to 2.
   *
   * @param x a value
   * @return the square root of the value
   */
  public static final double sqrtOf(double x) {
    return Math.sqrt(x);
  }

  /**
   * "sqrt" is an abbreviation for "square root." A number that is squared is multiplied by itself.
   * For example, when 2 is squared, the answer is 2×2, which is 4. When a number is square rooted,
   * the answer is the number that was squared to get it. So, the square root of 4 is 2. Similarly,
   * the square root of 2 is about 1.414213562373095 because 1.4142135623730952
   * (1.414213562373095 × 1.414213562373095) is close to 2.
   *
   * @param x a value
   * @return the square root of the value
   */
  public static final float sqrtOf(float x) {
    return (float) Math.sqrt(x);
  }

  /**
   * "sin" is an abbreviation for "sine." The sine of an angle is the ratio between the length of
   * the side that is opposite (across) the triangle from it and the length of the hypotenuse (the
   * side that is across from the right angle). In the picture above, the sine of angle A is equal
   * to side "opposite" divided by side "hypotenuse".
   *
   * @param x an angle between [0,...,360]
   * @return the sin of the angle
   */
  public static final double sinOf(double x) {
    return Math.sin(x / 180.0 * Math.PI);
  }

  /**
   * "sin" is an abbreviation for "sine." The sine of an angle is the ratio between the length of
   * the side that is opposite (across) the triangle from it and the length of the hypotenuse (the
   * side that is across from the right angle). In the picture above, the sine of angle A is equal
   * to side "opposite" divided by side "hypotenuse".
   *
   * @param x an angle between [0,...,360]
   * @return the sin of the angle
   */
  public static final float sinOf(float x) {
    return (float) Math.sin(x / 180.0 * Math.PI);
  }

  /**
   * "cos" is the abbreviation for "cosine." The cosine of an angle is the ratio between the length
   * of the side adjacent (next to) it on the triangle and the length of the hypotenuse. In the
   * picture above, the cosine of angle A is equal to side "adjacent" divided by side "hypotenuse".
   *
   * @param x an angle between [0, ..., 360]
   * @return the cos of the angle
   */
  public static final double cosOf(double x) {
    return Math.cos(x / 180.0 * Math.PI);
  }

  /**
   * "cos" is the abbreviation for "cosine." The cosine of an angle is the ratio between the length
   * of the side adjacent (next to) it on the triangle and the length of the hypotenuse. In the
   * picture above, the cosine of angle A is equal to side "adjacent" divided by side "hypotenuse".
   *
   * @param x an angle between [0, ..., 360]
   * @return the cos of the angle
   */
  public static final float cosOf(float x) {
    return (float) Math.cos(x / 180.0 * Math.PI);
  }

  /**
   * "tan" is the abbreviation for "tangent." The tangent of an angle is the ratio between the
   * length of the side adjacent to it and the side opposite of it. In the picture above, the
   * tangent of angle A is equal to side "opposite" divided by side "adjacent".
   *
   * @param x an angle between [0, ..., 360]
   * @return the tan of the angle
   */
  public static final double tanOf(double x) {
    return Math.tan(x / 180.0 * Math.PI);
  }

  /**
   * "tan" is the abbreviation for "tangent." The tangent of an angle is the ratio between the
   * length of the side adjacent to it and the side opposite of it. In the picture above, the
   * tangent of angle A is equal to side "opposite" divided by side "adjacent".
   *
   * @param x an angle between [0, ..., 360]
   * @return the tan of the angle
   */
  public static final float tanOf(float x) {
    return (float) Math.tan(x / 180.0 * Math.PI);
  }

  /**
   * "asin" is the abbreviation for "arcsine" and is also sometimes written as sin−1. When given the
   * ratio (in decimal form) of the length of the opposite side and hypotenuse of a right triangle,
   * it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final double asinOf(double x) {
    return Math.asin(x * 180.0 / Math.PI);
  }

  /**
   * "asin" is the abbreviation for "arcsine" and is also sometimes written as sin−1. When given the
   * ratio (in decimal form) of the length of the opposite side and hypotenuse of a right triangle,
   * it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final float asinOf(float x) {
    return (float) Math.asin(x * 180.0 / Math.PI);
  }

  /**
   * "acos" is the abbreviation for "arccosine" and is also sometimes written as cos−1. When given
   * the ratio (in decimal form) of the length of the adjacent side and hypotenuse of a right
   * triangle, it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final double acosOf(double x) {
    return Math.acos(x * 180.0 / Math.PI);
  }

  /**
   * "acos" is the abbreviation for "arccosine" and is also sometimes written as cos−1. When given
   * the ratio (in decimal form) of the length of the adjacent side and hypotenuse of a right
   * triangle, it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final float acosOf(float x) {
    return (float) Math.acos(x * 180.0 / Math.PI);
  }

  /**
   * "atan" is the abbreviation for "arctangent" and is also sometimes written as tan−1. When given
   * the ratio (in decimal form) of the length of the opposite side and adjacent side of a right
   * triangle, it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final double atanOf(double x) {
    return Math.atan(x * 180.0 / Math.PI);
  }

  /**
   * "atan" is the abbreviation for "arctangent" and is also sometimes written as tan−1. When given
   * the ratio (in decimal form) of the length of the opposite side and adjacent side of a right
   * triangle, it finds the angle.
   *
   * @param x a value
   * @return the angle of that value
   */
  public static final float atanOf(float x) {
    return (float) Math.atan(x * 180.0 / Math.PI);
  }

  /**
   * ln is "natural log." It figures out how many times e would have to be multiplied by itself to
   * get the value. For example, if the value was 148.4, the answer would be about 5 because e5
   * (e×e×e×e×e) is around 148.4.
   *
   * @param x a value
   * @return the ln of that value
   */
  public static final double lnOf(double x) {
    return Math.log(x);
  }

  /**
   * ln is "natural log." It figures out how many times e would have to be multiplied by itself to
   * get the value. For example, if the value was 148.4, the answer would be about 5 because e5
   * (e×e×e×e×e) is around 148.4.
   *
   * @param x a value
   * @return the ln of that value
   */
  public static final float lnOf(float x) {
    return (float) Math.log(x);
  }

  /**
   * "Log" is short for "logarithm." The log function figures out how many times 10 must be
   * multiplied by itself to get the value. For example, if the value is 100, the answer is 2
   * because 10×10 is 100.
   *
   * @param x a value
   * @return the log to base 10 of that value
   */
  public static final double logOf(double x) {
    return Math.log10(x);
  }

  /**
   * "Log" is short for "logarithm." The log function figures out how many times 10 must be
   * multiplied by itself to get the value. For example, if the value is 100, the answer is 2
   * because 10×10 is 100.
   *
   * @param x a value
   * @return the log to base 10 of that value
   */
  public static final float logOf(float x) {
    return (float) Math.log10(x);
  }

  /**
   * "e" is an abbreviation for "Euler's number", which is about 2.718. With the e^ function, e is
   * multiplied by itself the value number of times. For example, if the value is 3, the answer
   * would be e3, or e×e×e, which is about 20.086.
   *
   * @param x a value
   * @return e to the power of that value
   */
  public static final double eToThePowerOf(double x) {
    return Math.pow(Math.E, x);
  }

  /**
   * "e" is an abbreviation for "Euler's number", which is about 2.718. With the e^ function, e is
   * multiplied by itself the value number of times. For example, if the value is 3, the answer
   * would be e3, or e×e×e, which is about 20.086.
   *
   * @param x a value
   * @return e to the power of that value
   */
  public static final float eToThePowerOf(float x) {
    return (float) Math.pow(Math.E, x);
  }

  /**
   * The 10^ function multiplies 10 times itself the value number of times. For example, if the
   * value was 6, the answer would be 106 (that is 10×10×10×10×10×10), which is 1,000,000.
   *
   * @param x a value
   * @return 10 to the power of that value
   */
  public static final double tenToThePowerOf(double x) {
    return Math.pow(10, x);
  }

  /**
   * The 10^ function multiplies 10 times itself the value number of times. For example, if the
   * value was 6, the answer would be 106 (that is 10×10×10×10×10×10), which is 1,000,000.
   *
   * @param x a value
   * @return 10 to the power of that value
   */
  public static final float tenToThePowerOf(float x) {
    return (float) Math.pow(10, x);
  }
}
