package org.openpatch.scratch.internal.text;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Glyph bitmaps at device size (nominal size × window scale), for the renderer's glyph atlases
 * and for CPU captures. Grey coverage in normal mode; in pixel-art mode on whole device pixels and
 * without smoothing.
 */
public final class GlyphRasterizer {

  /**
   * A glyph's coverage.
   *
   * @param width bitmap width in device pixels
   * @param height bitmap height in device pixels
   * @param offsetX left edge relative to the pen position
   * @param offsetY top edge relative to the baseline (negative: above it)
   * @param coverage 0–255 per pixel, row by row, top first
   */
  public record GlyphBitmap(int width, int height, int offsetX, int offsetY, byte[] coverage) {}

  private GlyphRasterizer() {}

  /**
   * @param deviceSize the font size in device pixels
   * @param subpixelX the fractional part of the pen position (0–1), ignored in pixel-art mode
   */
  public static GlyphBitmap rasterize(Font font, int codepoint, double deviceSize, double subpixelX,
      boolean pixelArt) {
    float scale = (float) (deviceSize / font.getUnitsPerEm());
    float shift = pixelArt ? 0 : (float) (subpixelX - Math.floor(subpixelX));
    int glyph = font.glyph(codepoint);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer x0 = stack.mallocInt(1), y0 = stack.mallocInt(1), x1 = stack.mallocInt(1), y1 = stack.mallocInt(1);
      STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(font.info, glyph, scale, scale, shift, 0, x0, y0, x1, y1);
      int w = x1.get(0) - x0.get(0), h = y1.get(0) - y0.get(0);
      if (w <= 0 || h <= 0) {
        return new GlyphBitmap(0, 0, x0.get(0), y0.get(0), new byte[0]);
      }
      ByteBuffer out = MemoryUtil.memAlloc(w * h);
      try {
        STBTruetype.stbtt_MakeGlyphBitmapSubpixel(font.info, out, w, h, w, scale, scale, shift, 0, glyph);
        byte[] coverage = new byte[w * h];
        out.get(coverage);
        if (pixelArt) {
          for (int i = 0; i < coverage.length; i++) {
            coverage[i] = (coverage[i] & 0xff) >= 128 ? (byte) 255 : 0;
          }
        }
        return new GlyphBitmap(w, h, x0.get(0), y0.get(0), coverage);
      } finally {
        MemoryUtil.memFree(out);
      }
    }
  }
}
