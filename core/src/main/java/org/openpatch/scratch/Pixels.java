package org.openpatch.scratch;

import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;

/**
 * The pixels of the stage or of an image file, to read colours from. Coordinates count pixels
 * from the top left corner (0, 0).
 *
 * <pre>{@code
 * Pixels terrain = Pixels.of("assets/terrain.png");
 * boolean solid = terrain.getColor(10, 20).getAlpha() > 0;
 *
 * Pixels screen = Pixels.of(this.getStage());
 * }</pre>
 *
 * <p>The stage's pixels are what its world layers show at that moment: backdrop, stage colour,
 * pen strokes, stamps and sprites with their effects, the same as colour sensing sees. Texts,
 * shaders and the UI layer are not included.
 */
public final class Pixels {

  private final int width, height;
  private final int[] argb;

  private Pixels(int width, int height, int[] argb) {
    this.width = width;
    this.height = height;
    this.argb = argb;
  }

  /**
   * The stage's pixels as they are now.
   *
   * @param stage the stage
   * @return its pixels, at stage size
   */
  public static Pixels of(Stage stage) {
    return new Pixels(stage.getWidth(), stage.getHeight(), stage.compositor(null).render());
  }

  /**
   * The pixels of an image file.
   *
   * @param imagePath the image
   * @return its pixels
   */
  public static Pixels of(String imagePath) {
    Image image = Images.load(imagePath);
    return new Pixels(image.getWidth(), image.getHeight(), image.getArgb().clone());
  }

  /** @return the width in pixels */
  public int getWidth() {
    return this.width;
  }

  /** @return the height in pixels */
  public int getHeight() {
    return this.height;
  }

  /**
   * @param x pixels from the left edge
   * @param y pixels from the top edge
   * @return the colour there, or transparent black outside
   */
  public Color getColor(int x, int y) {
    return Color.fromARGB(this.getArgb(x, y));
  }

  /**
   * @param x pixels from the left edge
   * @param y pixels from the top edge
   * @return the colour there packed as alpha, red, green, blue, or 0 outside
   */
  public int getArgb(int x, int y) {
    return x < 0 || y < 0 || x >= this.width || y >= this.height ? 0 : this.argb[y * this.width + x];
  }
}
