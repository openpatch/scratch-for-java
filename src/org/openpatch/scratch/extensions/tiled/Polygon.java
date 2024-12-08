package org.openpatch.scratch.extensions.tiled;

/**
 * The Polygon class represents a polygon object in Tiled.
 * It includes a list of points that make up the polygon.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * Polygon p = new Polygon();
 * p.points = "0,0 10,10";
 * }
 * </pre>
 */
public class Polygon {
  /**
   * The points that make up the polygon.
   */
  public String points;

  /**
   * Returns the points that make up the polygon as an array of double arrays.
   *
   * @return the points that make up the polygon
   */
  public double[][] getPoints() {
    var ps = points.split(" ");
    var pi = new double[ps.length][2];

    for (int i = 0; i < pi.length; i++) {
      var s = ps[i].split(",");
      var x = Double.parseDouble(s[0]);
      var y = Double.parseDouble(s[1]);
      pi[i][0] = x;
      pi[i][1] = y;
    }

    return pi;
  }
}
