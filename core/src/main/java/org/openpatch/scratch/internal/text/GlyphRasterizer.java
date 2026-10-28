package org.openpatch.scratch.internal.text;

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
    int[] box = font.info.bitmapBox(glyph, scale, scale, shift, 0);
    int w = box[2] - box[0], h = box[3] - box[1];
    if (w <= 0 || h <= 0) {
      return new GlyphBitmap(0, 0, box[0], box[1], new byte[0]);
    }
    byte[] coverage = new byte[w * h];
    font.info.makeBitmap(coverage, w, h, w, scale, scale, shift, 0, glyph);
    if (pixelArt) {
      for (int i = 0; i < coverage.length; i++) {
        coverage[i] = (coverage[i] & 0xff) >= 128 ? (byte) 255 : 0;
      }
    }
    return new GlyphBitmap(w, h, box[0], box[1], coverage);
  }
}
