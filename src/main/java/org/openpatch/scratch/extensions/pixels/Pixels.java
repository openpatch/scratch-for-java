package org.openpatch.scratch.extensions.pixels;

import processing.core.PGraphics;

/**
 * The colours of everything a stage has drawn, one number per pixel.
 *
 * <p>
 * Reached through {@code stage.getPixels()}:
 *
 * <pre>{@code
 * int[] pixels = this.getPixels().main();
 * }</pre>
 */
public class Pixels {

  private final PGraphics mainBuffer;
  private final PGraphics backgroundBuffer;
  private final PGraphics foregroundBuffer;

  /**
   * Creates the pixel access for a stage.
   *
   * @param mainBuffer       where the sprites are drawn
   * @param backgroundBuffer where the background is drawn
   * @param foregroundBuffer where the foreground is drawn
   */
  public Pixels(PGraphics mainBuffer, PGraphics backgroundBuffer, PGraphics foregroundBuffer) {
    this.mainBuffer = mainBuffer;
    this.backgroundBuffer = backgroundBuffer;
    this.foregroundBuffer = foregroundBuffer;
  }

  /**
   * Returns the pixels of the layer the sprites are drawn on.
   *
   * @return one colour per pixel
   */
  public int[] main() {
    this.mainBuffer.loadPixels();
    return this.mainBuffer.pixels;
  }

  /**
   * Returns the pixels of the background layer.
   *
   * @return one colour per pixel
   */
  public int[] background() {
    this.backgroundBuffer.loadPixels();
    return this.backgroundBuffer.pixels;
  }

  /**
   * Returns the pixels of the foreground layer.
   *
   * @return one colour per pixel
   */
  public int[] foreground() {
    this.foregroundBuffer.loadPixels();
    return this.foregroundBuffer.pixels;
  }
}
