package org.openpatch.scratch.internal.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Polygon geometry for shapes and hitboxes, without {@code java.awt.geom}. A polygon is one closed
 * ring of points {@code {x0, y0, x1, y1, …}}, its last point not repeating the first. Plain arrays
 * and plain maths, so the online IDE can port it line by line.
 *
 * <p>The rule for overlapping is the one 6.x had: shapes that only touch (along an edge or at a
 * point) do not overlap; their overlap has no area.
 */
public final class Geometry {

  private Geometry() {}

  /** Whether the ring is convex; collinear points are allowed, the winding does not matter. */
  public static boolean isConvex(double[] ring) {
    int n = ring.length / 2;
    if (n < 3) {
      return false;
    }
    int sign = 0;
    for (int i = 0; i < n; i++) {
      int j = (i + 1) % n, k = (i + 2) % n;
      double cross = (ring[2 * j] - ring[2 * i]) * (ring[2 * k + 1] - ring[2 * j + 1])
          - (ring[2 * j + 1] - ring[2 * i + 1]) * (ring[2 * k] - ring[2 * j]);
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
    return sign != 0;
  }

  /** The signed area (shoelace); positive for anticlockwise rings in a y-up system. */
  public static double signedArea(double[] ring) {
    int n = ring.length / 2;
    double sum = 0;
    for (int i = 0; i < n; i++) {
      int j = (i + 1) % n;
      sum += ring[2 * i] * ring[2 * j + 1] - ring[2 * j] * ring[2 * i + 1];
    }
    return sum / 2;
  }

  /**
   * Convex pieces covering the ring: the ring itself if it is convex, else its triangles by ear
   * clipping (simple polygons, concave allowed, either winding). Degenerate pieces are dropped.
   */
  public static double[][] convexPieces(double[] ring) {
    if (isConvex(ring)) {
      return new double[][] {ring};
    }
    return triangulate(ring);
  }

  /** Ear clipping. A self-intersecting ring gives a best effort: whatever ears can be found. */
  public static double[][] triangulate(double[] ring) {
    int n = ring.length / 2;
    List<Integer> left = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      left.add(i);
    }
    double orientation = Math.signum(signedArea(ring));
    List<double[]> triangles = new ArrayList<>();
    int guard = 0;
    while (left.size() > 3 && guard < n * n) {
      guard++;
      boolean clipped = false;
      for (int i = 0; i < left.size(); i++) {
        int a = left.get((i + left.size() - 1) % left.size()), b = left.get(i), c = left.get((i + 1) % left.size());
        double cross = cross(ring, a, b, c);
        if (cross == 0) {
          left.remove(i); // a straight point is no corner at all
          clipped = true;
          break;
        }
        if (Math.signum(cross) != orientation) {
          continue; // reflex corner
        }
        if (anyInside(ring, left, a, b, c)) {
          continue;
        }
        triangles.add(new double[] {ring[2 * a], ring[2 * a + 1], ring[2 * b], ring[2 * b + 1], ring[2 * c],
            ring[2 * c + 1]});
        left.remove(i);
        clipped = true;
        break;
      }
      if (!clipped) {
        break;
      }
    }
    if (left.size() == 3) {
      int a = left.get(0), b = left.get(1), c = left.get(2);
      if (cross(ring, a, b, c) != 0) {
        triangles.add(new double[] {ring[2 * a], ring[2 * a + 1], ring[2 * b], ring[2 * b + 1], ring[2 * c],
            ring[2 * c + 1]});
      }
    }
    return triangles.toArray(new double[0][]);
  }

  private static double cross(double[] r, int a, int b, int c) {
    return (r[2 * b] - r[2 * a]) * (r[2 * c + 1] - r[2 * b + 1]) - (r[2 * b + 1] - r[2 * a + 1]) * (r[2 * c] - r[2 * b]);
  }

  /** Whether another remaining point lies inside or on the triangle a, b, c. */
  private static boolean anyInside(double[] r, List<Integer> left, int a, int b, int c) {
    for (int p : left) {
      if (p == a || p == b || p == c) {
        continue;
      }
      double px = r[2 * p], py = r[2 * p + 1];
      if (px == r[2 * a] && py == r[2 * a + 1] || px == r[2 * b] && py == r[2 * b + 1]
          || px == r[2 * c] && py == r[2 * c + 1]) {
        continue; // a repeated point is not in the way
      }
      double d1 = side(px, py, r[2 * a], r[2 * a + 1], r[2 * b], r[2 * b + 1]);
      double d2 = side(px, py, r[2 * b], r[2 * b + 1], r[2 * c], r[2 * c + 1]);
      double d3 = side(px, py, r[2 * c], r[2 * c + 1], r[2 * a], r[2 * a + 1]);
      boolean negative = d1 < 0 || d2 < 0 || d3 < 0, positive = d1 > 0 || d2 > 0 || d3 > 0;
      if (!(negative && positive)) {
        return true;
      }
    }
    return false;
  }

  private static double side(double px, double py, double ax, double ay, double bx, double by) {
    return (px - bx) * (ay - by) - (ax - bx) * (py - by);
  }

  /**
   * Separating axes for two convex rings: they overlap unless an edge normal separates them.
   * Touching counts as apart ({@code aMax <= bMin}), as in 6.x.
   */
  public static boolean convexOverlap(double[] a, double[] b) {
    return !separated(a, b) && !separated(b, a);
  }

  private static boolean separated(double[] a, double[] b) {
    int n = a.length / 2;
    for (int i = 0; i < n; i++) {
      int j = (i + 1) % n;
      double axisX = a[2 * j + 1] - a[2 * i + 1];
      double axisY = a[2 * i] - a[2 * j];
      if (axisX == 0 && axisY == 0) {
        continue;
      }
      double aMin = Double.POSITIVE_INFINITY, aMax = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < a.length; k += 2) {
        double p = a[k] * axisX + a[k + 1] * axisY;
        aMin = Math.min(aMin, p);
        aMax = Math.max(aMax, p);
      }
      double bMin = Double.POSITIVE_INFINITY, bMax = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < b.length; k += 2) {
        double p = b[k] * axisX + b[k + 1] * axisY;
        bMin = Math.min(bMin, p);
        bMax = Math.max(bMax, p);
      }
      if (aMax <= bMin || bMax <= aMin) {
        return true;
      }
    }
    return false;
  }

  /** Whether any convex piece of one overlaps any of the other. */
  public static boolean overlap(double[][] piecesA, double[][] piecesB) {
    for (double[] a : piecesA) {
      for (double[] b : piecesB) {
        if (convexOverlap(a, b)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Point in polygon by crossing number (even-odd); deterministic on the boundary. */
  public static boolean contains(double[] ring, double x, double y) {
    int n = ring.length / 2;
    boolean inside = false;
    for (int i = 0, j = n - 1; i < n; j = i++) {
      double xi = ring[2 * i], yi = ring[2 * i + 1], xj = ring[2 * j], yj = ring[2 * j + 1];
      if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
        inside = !inside;
      }
    }
    return inside;
  }

  /** {minX, minY, maxX, maxY}; all zero for an empty ring. */
  public static double[] bounds(double[] ring) {
    if (ring.length < 2) {
      return new double[4];
    }
    double minX = ring[0], minY = ring[1], maxX = ring[0], maxY = ring[1];
    for (int i = 2; i < ring.length; i += 2) {
      minX = Math.min(minX, ring[i]);
      maxX = Math.max(maxX, ring[i]);
      minY = Math.min(minY, ring[i + 1]);
      maxY = Math.max(maxY, ring[i + 1]);
    }
    return new double[] {minX, minY, maxX, maxY};
  }

  /** The ring through the affine map {@code x' = a·x + c·y + e}, {@code y' = b·x + d·y + f}. */
  public static double[] transform(double[] ring, double a, double b, double c, double d, double e, double f) {
    double[] out = new double[ring.length];
    for (int i = 0; i < ring.length; i += 2) {
      double x = ring[i], y = ring[i + 1];
      out[i] = a * x + c * y + e;
      out[i + 1] = b * x + d * y + f;
    }
    return out;
  }

  /**
   * An ellipse as a polygon whose corners lie on it and whose edges stay within 0.25 of it: enough
   * segments that the sagitta {@code r (1 − cos(π / n))} is at most 0.25 (between 12 and 256).
   */
  public static double[] ellipse(double cx, double cy, double rx, double ry) {
    double r = Math.max(Math.abs(rx), Math.abs(ry));
    int segments = r <= 0.25 ? 12
        : (int) Math.max(12, Math.min(256, Math.ceil(Math.PI / Math.acos(1 - 0.25 / r))));
    // a multiple of 4 puts corners on the four extremes, so the bounds are exact
    segments = (segments + 3) / 4 * 4;
    double[] ring = new double[2 * segments];
    for (int i = 0; i < segments; i++) {
      double t = 2 * Math.PI * i / segments;
      ring[2 * i] = cx + rx * Math.cos(t);
      ring[2 * i + 1] = cy + ry * Math.sin(t);
    }
    return ring;
  }
}
