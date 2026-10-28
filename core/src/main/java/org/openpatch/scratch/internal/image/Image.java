package org.openpatch.scratch.internal.image;

import java.io.IOException;
import java.nio.file.Path;
import org.openpatch.scratch.internal.platform.Platform;

/**
 * An image on the CPU: ARGB pixels (straight alpha, {@code 0xAARRGGBB}), row by row, top row
 * first. Costumes share their images and never change them, so the renderer may cache textures by
 * image identity.
 */
public final class Image {

  private final int width, height;
  private final int[] argb;

  /** A transparent image. */
  public Image(int width, int height) {
    this(width, height, new int[width * height]);
  }

  /** An image of these pixels; the array is used, not copied. */
  public Image(int width, int height, int[] argb) {
    if (width < 0 || height < 0 || argb.length != width * height) {
      throw new IllegalArgumentException("An image of " + width + " x " + height + " needs " + width * height
          + " pixels, not " + argb.length + ".");
    }
    this.width = width;
    this.height = height;
    this.argb = argb;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  /** The pixels themselves, not a copy. */
  public int[] getArgb() {
    return this.argb;
  }

  public int get(int x, int y) {
    return this.argb[y * this.width + x];
  }

  public void set(int x, int y, int argb) {
    this.argb[y * this.width + x] = argb;
  }

  public Image copy() {
    return new Image(this.width, this.height, this.argb.clone());
  }

  /** A copy of a part, e.g. one frame of a sprite sheet; parts outside the image are transparent. */
  public Image region(int x, int y, int w, int h) {
    int[] out = new int[w * h];
    for (int row = 0; row < h; row++) {
      int sy = y + row;
      if (sy < 0 || sy >= this.height) {
        continue;
      }
      for (int col = 0; col < w; col++) {
        int sx = x + col;
        if (sx >= 0 && sx < this.width) {
          out[row * w + col] = this.argb[sy * this.width + sx];
        }
      }
    }
    return new Image(w, h, out);
  }

  /** A copy flipped left to right. */
  public Image mirrorX() {
    int[] out = new int[this.argb.length];
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        out[y * this.width + this.width - 1 - x] = this.argb[y * this.width + x];
      }
    }
    return new Image(this.width, this.height, out);
  }

  /** Writes a PNG, creating its folder. */
  public void writePng(Path file) throws IOException {
    Platform.get().writePng(file, new Platform.Pixmap(this.width, this.height, this.argb));
  }

  /**
   * Decodes PNG, JPEG or GIF (first frame) from memory, the same way for files, the classpath and
   * jars, with the platform's decoder (stb_image on the desktop, the browser's in a page).
   *
   * @param encoded the file's bytes
   * @param name for error messages
   */
  public static Image decode(byte[] encoded, String name) throws IOException {
    Platform.Pixmap pixels = Platform.get().decodeImage(encoded, name);
    return new Image(pixels.width(), pixels.height(), pixels.argb());
  }
}
