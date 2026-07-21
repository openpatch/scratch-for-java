package org.openpatch.scratch;

import processing.core.PApplet;

/**
 * Where a piece of text sits relative to its position.
 *
 * <pre>{@code
 * myText.setAlign(TextAlign.LEFT);
 * }</pre>
 *
 * @see Text#setAlign(TextAlign)
 */
public enum TextAlign {

  /** Whatever the text style already uses. */
  DEFAULT(-1),

  /** Centred on its position. */
  CENTER(PApplet.CENTER),

  /** Its right edge at its position. */
  RIGHT(PApplet.RIGHT),

  /** Its left edge at its position. */
  LEFT(PApplet.LEFT);

  private final int mode;

  TextAlign(int mode) {
    this.mode = mode;
  }

  /**
   * Returns the number the graphics library uses for this alignment.
   *
   * @return the alignment number
   *
   * @ignore-in-docs
   */
  public int getMode() {
    return this.mode;
  }
}
