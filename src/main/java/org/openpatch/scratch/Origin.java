package org.openpatch.scratch;

/**
 * The Origin enum represents the different origin positions that a sprite can have.
 * The origin determines the reference point for positioning and rotation.
 */
public enum Origin {
  /** Origin at the top-left corner */
  TOP_LEFT,
  /** Origin at the top-center */
  TOP_CENTER,
  /** Origin at the top-right corner */
  TOP_RIGHT,
  /** Origin at the center-left */
  CENTER_LEFT,
  /** Origin at the center (default) */
  CENTER,
  /** Origin at the center-right */
  CENTER_RIGHT,
  /** Origin at the bottom-left corner */
  BOTTOM_LEFT,
  /** Origin at the bottom-center */
  BOTTOM_CENTER,
  /** Origin at the bottom-right corner */
  BOTTOM_RIGHT,
  /** Custom origin position */
  CUSTOM
}
