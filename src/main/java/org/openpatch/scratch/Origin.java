package org.openpatch.scratch;

/**
 * The Origin enum represents the different origin positions that a sprite can have.
 * The origin determines the reference point for the sprite's position and rotation.
 * By default, the origin is at the center of the sprite.
 */
public enum Origin {
  /** Origin at the top-left corner of the sprite */
  TOP_LEFT,
  /** Origin at the top-center of the sprite */
  TOP_CENTER,
  /** Origin at the top-right corner of the sprite */
  TOP_RIGHT,
  /** Origin at the center-left of the sprite */
  CENTER_LEFT,
  /** Origin at the center of the sprite (default) */
  CENTER,
  /** Origin at the center-right of the sprite */
  CENTER_RIGHT,
  /** Origin at the bottom-left corner of the sprite */
  BOTTOM_LEFT,
  /** Origin at the bottom-center of the sprite */
  BOTTOM_CENTER,
  /** Origin at the bottom-right corner of the sprite */
  BOTTOM_RIGHT,
  /** Custom origin position specified by x and y offsets */
  CUSTOM
}
