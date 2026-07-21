package org.openpatch.scratch;

/**
 * How pictures are smoothed when they are drawn larger or smaller than they
 * really are.
 *
 * <p>
 * The default, {@link #BILINEAR}, suits most projects. Pixel art is the usual
 * reason to change it: {@link #POINT} keeps the pixels sharp and square instead
 * of blurring them together.
 *
 * <pre>{@code
 * Window.useTextureSampling(TextureSampling.POINT);
 * new MyStage();
 * }</pre>
 *
 * @see Window#useTextureSampling(TextureSampling)
 */
public enum TextureSampling {

  /**
   * Nearest pixel, whether the picture is drawn larger or smaller. Keeps pixel
   * art crisp.
   */
  POINT(2),

  /**
   * Nearest pixel when drawn larger, smoothed when drawn smaller.
   */
  LINEAR(3),

  /**
   * Smoothed both ways, using one level of detail at a time. This is the
   * default.
   */
  BILINEAR(4),

  /**
   * Smoothed both ways and between levels of detail, which gives the best
   * quality when a picture is drawn much smaller than it is.
   */
  TRILINEAR(5);

  private final int mode;

  TextureSampling(int mode) {
    this.mode = mode;
  }

  /**
   * Returns the number the graphics library uses for this setting.
   *
   * @return the mode number
   *
   * @ignore-in-docs
   */
  public int getMode() {
    return this.mode;
  }
}
