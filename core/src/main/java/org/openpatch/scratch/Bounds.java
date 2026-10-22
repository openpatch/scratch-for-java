package org.openpatch.scratch;

/**
 * A rectangle along the axes: the bounds of a shape.
 *
 * @param x the smallest x
 * @param y the smallest y
 * @param width the width
 * @param height the height
 */
public record Bounds(double x, double y, double width, double height) {

  /**
   * @param other another rectangle
   * @return whether the two overlap with some area; rectangles that only touch do not
   */
  public boolean intersects(Bounds other) {
    return this.x < other.x + other.width && this.x + this.width > other.x && this.y < other.y + other.height
        && this.y + this.height > other.y;
  }
}
