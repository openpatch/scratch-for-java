package org.openpatch.scratch;

/**
 * One of the three layers a stage draws, from back to front.
 *
 * <p>
 * Sprites live between the background and the foreground. Anything stamped onto
 * a layer stays there until the layer is erased.
 *
 * <pre>{@code
 * map.stampLayer("ground", Layer.BACKGROUND);
 * }</pre>
 */
public enum Layer {

  /** Behind the sprites. */
  BACKGROUND,

  /** In front of the sprites. */
  FOREGROUND,

  /** In front of everything, and unaffected by the camera. */
  UI
}
