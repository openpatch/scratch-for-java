package org.openpatch.scratch.internal.image;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.Natives;

/**
 * An image on the CPU: ARGB pixels (straight alpha, {@code 0xAARRGGBB}), row by row, top row
 * first. Costumes share their images and never change them, so the renderer may cache textures by
 * image identity.
 */
public final class Image {

  static {
    Natives.init();
  }

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

  /** Writes a PNG with stb_image_write. */
  public void writePng(Path file) throws IOException {
    ByteBuffer rgba = MemoryUtil.memAlloc(Math.max(1, this.argb.length * 4));
    try {
      for (int p : this.argb) {
        rgba.put((byte) (p >> 16)).put((byte) (p >> 8)).put((byte) p).put((byte) (p >>> 24));
      }
      rgba.flip();
      Path absolute = file.toAbsolutePath();
      if (absolute.getParent() != null) {
        java.nio.file.Files.createDirectories(absolute.getParent());
      }
      if (!STBImageWrite.stbi_write_png(absolute.toString(), this.width, this.height, 4, rgba, this.width * 4)) {
        throw new IOException("Could not write " + file);
      }
    } finally {
      MemoryUtil.memFree(rgba);
    }
  }

  /**
   * Decodes PNG, JPEG, GIF (first frame), BMP or TGA from memory, the same way for files, the
   * classpath and jars. No global stb flags: loading runs on several threads at once.
   *
   * @param encoded the file's bytes
   * @param name for error messages
   */
  public static Image decode(byte[] encoded, String name) throws IOException {
    ByteBuffer in = MemoryUtil.memAlloc(Math.max(1, encoded.length));
    try (MemoryStack stack = MemoryStack.stackPush()) {
      in.put(encoded).flip();
      IntBuffer w = stack.mallocInt(1), h = stack.mallocInt(1), channels = stack.mallocInt(1);
      ByteBuffer rgba = STBImage.stbi_load_from_memory(in, w, h, channels, 4);
      if (rgba == null) {
        throw new IOException("Could not decode " + name + ": " + STBImage.stbi_failure_reason());
      }
      try {
        int width = w.get(0), height = h.get(0);
        int[] argb = new int[width * height];
        for (int i = 0; i < argb.length; i++) {
          int r = rgba.get(4 * i) & 0xff, g = rgba.get(4 * i + 1) & 0xff, b = rgba.get(4 * i + 2) & 0xff;
          int a = rgba.get(4 * i + 3) & 0xff;
          argb[i] = a << 24 | r << 16 | g << 8 | b;
        }
        return new Image(width, height, argb);
      } finally {
        STBImage.stbi_image_free(rgba);
      }
    } finally {
      MemoryUtil.memFree(in);
    }
  }
}
