package org.openpatch.scratch.extensions.math;

import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.OpenSimplex2S;

public class Random {
  private static java.util.Random internalRandom;
  private static long noiseSeed = 1L;

  /*
   *
   * Returns the Open Simplex noise value at specified coordinates
   *
   * @param x x-coordinate
   *
   */
  public static double noise(double x) {
    return OpenSimplex2S.noise2(noiseSeed, x, x);
  }

  /*
   *
   * Returns the Open Simplex noise value at specified coordinates
   *
   * @param x x-coordinate
   *
   * @param y y-coordinate
   *
   */
  public static double noise(double x, double y) {
    return OpenSimplex2S.noise2(noiseSeed, x, y);
  }

  /*
   *
   * Returns the Open Simplex noise value at specified coordinates
   *
   * @param x x-coordinate
   *
   * @param y y-coordinate
   *
   * @param z z-coordinate
   *
   * @see Random#noiseSeed(long)
   *
   */
  public static double noise(double x, double y, double z) {
    return OpenSimplex2S.noise3_ImproveXY(noiseSeed, x, y, z);
  }

  /**
   * Sets the seed for the noise method.
   *
   * @param noiseSeed the seed
   */
  public static void noiseSeed(long noiseSeed) {
    Random.noiseSeed = noiseSeed;
  }

  /**
   * Returns a random unit vector
   *
   * @return a random unit vector
   */
  public static Vector2 randomVector2() {
    return new Vector2(random(), random()).unitVector();
  }

  /**
   * Return a random x coordinate with respect to the width of the window.
   *
   * @return a random x coordinate
   */
  public static double randomX() {
    return Random.random(
        -Applet.getInstance().getWidth() / 2.0, Applet.getInstance().getWidth() / 2.0);
  }

  /**
   * Return a random y coordinate with respect to the width of the window.
   *
   * @return a random y coordinate
   */
  public static double randomY() {
    return Random.random(
        -Applet.getInstance().getHeight() / 2.0, Applet.getInstance().getHeight() / 2.0);
  }

  /**
   * Returns a random position with respect to the width of the window.
   *
   * @return a random position vector
   */
  public static Vector2 randomPosition() {
    return new Vector2(randomX(), randomY());
  }

  /**
   * Returns a random double between 0 and 1.
   *
   * @return a random double
   */
  public static double random() {
    return getRandom().nextDouble();
  }

  /**
   * Returns a random integer between 0 and max.
   *
   * @param max the maximum integer
   * @return a random integer
   */
  public static int randomInt(int max) {
    return getRandom().nextInt(max);
  }

  /**
   * Returns a random double between 0 and max.
   *
   * @param max the maximum double
   * @return a random double
   */
  public static double random(double max) {
    return getRandom().nextDouble() * max;
  }

  /**
   * Returns a random integer between min and max.
   *
   * @param min the minimum integer
   * @param max the maximum integer
   * @return a random integer
   */
  public static int randomInt(int min, int max) {
    return getRandom().nextInt(max - min) + min;
  }

  /**
   * Returns a random double between min and max.
   *
   * @param min the minimum double
   * @param max the maximum double
   * @return a random double
   */
  public static double random(double min, double max) {
    return getRandom().nextDouble() * (max - min) + min;
  }

  private static java.util.Random getRandom() {
    if (internalRandom == null) {
      internalRandom = new java.util.Random();
    }
    return internalRandom;
  }

  /**
   * Sets the seed for the random method.
   *
   * @param seed the seed
   */
  public static void randomSeed(long seed) {
    getRandom().setSeed(seed);
  }
}
