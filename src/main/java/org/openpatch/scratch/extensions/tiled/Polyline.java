package org.openpatch.scratch.extensions.tiled;

import java.util.List;

/**
 * The Polyline class represents a polyline object in Tiled. It includes a list of points that make
 * up the polyline.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Polyline p = new Polyline();
 * p.points = new ArrayList<Point>();
 * p.points.add(new Point(0, 0));
 * p.points.add(new Point(10, 10));
 * }</pre>
 */
public class Polyline {
  /** The list of points that make up the polyline. */
  public List<Point> points;
}
