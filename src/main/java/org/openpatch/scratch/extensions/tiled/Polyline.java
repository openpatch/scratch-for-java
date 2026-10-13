package org.openpatch.scratch.extensions.tiled;

/**
 * The Polyline class represents a polyline object in Tiled: its points as
 * Tiled writes them.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * Polyline p = new Polyline();
 * p.points = "0,0 10,10";
 * }</pre>
 */
class Polyline {
  /** The points of the polyline as Tiled writes them: "x,y x,y ...". */
  public String points;
}
