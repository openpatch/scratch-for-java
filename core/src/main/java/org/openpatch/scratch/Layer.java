package org.openpatch.scratch;

/**
 * One of the four layers a stage draws, from back to front, after its backdrop.
 *
 * <p>Every layer holds sprites, in Scratch's order within the layer ({@code goToFrontLayer()}
 * means the front of the sprite's own layer), and a canvas for pen strokes and stamps, created
 * when a pen or stamp first uses it. Sprites, texts and pens are in {@link #MAIN} unless they are
 * moved with {@code setLayer}; a pen draws onto the canvas of its own layer, which for a sprite's
 * pen is {@link #BACKGROUND}, behind the sprites, as in Scratch.
 *
 * <pre>{@code
 * this.setLayer(Layer.FOREGROUND);
 * }</pre>
 */
public enum Layer {

  /** Behind the sprites of {@link #MAIN}: scenery, and where pens draw by default. */
  BACKGROUND,

  /** Where sprites are unless they are moved. */
  MAIN,

  /** In front of the sprites of {@link #MAIN}. */
  FOREGROUND,

  /**
   * In front of everything and unaffected by the camera: buttons, scores, menus. Drawn at the
   * window's resolution, so it stays sharp. Colour sensing and {@code isTouchingSprite} between
   * the world and the UI ignore it.
   */
  UI
}
