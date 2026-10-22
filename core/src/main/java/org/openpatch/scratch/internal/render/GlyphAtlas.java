package org.openpatch.scratch.internal.render;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.GlyphRasterizer;
import org.openpatch.scratch.internal.text.GlyphRasterizer.GlyphBitmap;

/**
 * Glyph coverage rasterised at device size, packed into atlas pages on demand: no fixed
 * font sizes. Shelf packing; a full page starts the next. Window thread only.
 */
final class GlyphAtlas {

  static final int PAGE = 1024;
  /** Sub-pixel positions per device pixel in normal mode. */
  static final int SUBPIXELS = 4;

  /** Where a glyph is: its page, its pixels on the page, its offset from the pen position. */
  record Slot(int page, int x, int y, int width, int height, int offsetX, int offsetY) {}

  private record Key(Font font, int codepoint, int deviceSize100, int subpixel, boolean pixelArt) {}

  private final Map<Key, Slot> slots = new HashMap<>();
  private final java.util.List<Integer> pages = new java.util.ArrayList<>();
  private int shelfX, shelfY, shelfHeight;

  int page(int index) {
    return this.pages.get(index);
  }

  /** The glyph at a device size and sub-pixel offset (0 ≤ subpixel < 1), rasterised once. */
  Slot glyph(Font font, int codepoint, double deviceSize, double subpixel, boolean pixelArt) {
    int bucket = pixelArt ? 0 : (int) Math.floor(subpixel * SUBPIXELS) % SUBPIXELS;
    Key key = new Key(font, codepoint, (int) Math.round(deviceSize * 100), bucket, pixelArt);
    Slot slot = this.slots.get(key);
    if (slot == null) {
      GlyphBitmap bitmap = GlyphRasterizer.rasterize(font, codepoint, deviceSize, bucket / (double) SUBPIXELS,
          pixelArt);
      slot = this.place(bitmap);
      this.slots.put(key, slot);
    }
    return slot;
  }

  private Slot place(GlyphBitmap bitmap) {
    int w = bitmap.width(), h = bitmap.height();
    if (w == 0 || h == 0) {
      return new Slot(-1, 0, 0, 0, 0, bitmap.offsetX(), bitmap.offsetY());
    }
    if (w + 2 > PAGE || h + 2 > PAGE) {
      return new Slot(-1, 0, 0, 0, 0, 0, 0); // larger than a page: not drawn
    }
    if (this.pages.isEmpty() || this.shelfX + w + 1 > PAGE) {
      this.shelfX = 1;
      this.shelfY += this.shelfHeight + 1;
      this.shelfHeight = 0;
    }
    if (this.pages.isEmpty() || this.shelfY + h + 1 > PAGE) {
      this.pages.add(Gpu.coverageTexture(PAGE, PAGE));
      this.shelfX = 1;
      this.shelfY = 1;
      this.shelfHeight = 0;
    }
    int page = this.pages.size() - 1;
    ByteBuffer pixels = MemoryUtil.memAlloc(w * h).put(bitmap.coverage()).flip();
    try {
      glBindTexture(GL_TEXTURE_2D, this.pages.get(page));
      glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
      glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
      glTexSubImage2D(GL_TEXTURE_2D, 0, this.shelfX, this.shelfY, w, h, GL_RED, GL_UNSIGNED_BYTE, pixels);
      glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    } finally {
      MemoryUtil.memFree(pixels);
    }
    Slot slot = new Slot(page, this.shelfX, this.shelfY, w, h, bitmap.offsetX(), bitmap.offsetY());
    this.shelfX += w + 1;
    this.shelfHeight = Math.max(this.shelfHeight, h);
    return slot;
  }

  void delete() {
    for (int page : this.pages) {
      glDeleteTextures(page);
    }
    this.pages.clear();
    this.slots.clear();
  }
}
