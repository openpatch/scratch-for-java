package org.openpatch.scratch.internal;

import org.openpatch.scratch.internal.image.Image;

/**
 * A named costume or backdrop. Its image is shared with clones and never changed.
 *
 * @param name the name learners switch to
 * @param image the pixels
 */
public record Costume(String name, Image image) {

  /**
   * The default hitbox: the rectangle around the painted pixels (alpha above 0), as a ring in
   * costume pixels (origin top left, y down). Costumes are often drawn into a larger canvas, and
   * touching that empty space looks like a bug. A costume without any painted pixel gets its full
   * rectangle.
   */
  public double[] outline() {
    return ContentBounds.of(this.image);
  }
}
