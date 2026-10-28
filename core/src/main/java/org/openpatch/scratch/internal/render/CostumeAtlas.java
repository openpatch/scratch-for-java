package org.openpatch.scratch.internal.render;

import static org.openpatch.scratch.internal.platform.GL.*;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.openpatch.scratch.internal.image.Image;

/**
 * Costumes packed into shared textures, so sprites with different costumes are drawn in one go.
 * Each costume gets a cell on an 8-pixel grid with an 8-pixel border repeating its edge pixels:
 * smooth sampling and the three mip levels kept stay inside the cell, so a costume looks as it
 * does in a texture of its own (clamped at its edges). Costumes larger than {@link #MAX_SIZE}
 * keep a texture of their own. Window thread only.
 */
final class CostumeAtlas {

  static final int PAGE = 2048;
  /** The border around a costume, also the grid cells start on: 2 to the number of mip levels. */
  static final int PAD = 8;
  static final int LEVELS = 3;
  static final int MAX_SIZE = 512;

  /** Where a costume lies: a page's texture and the costume's corners in its coordinates. */
  record Region(int texture, double u0, double v0, double u1, double v1) {

    double u(double u) {
      return this.u0 + u * (this.u1 - this.u0);
    }

    double v(double v) {
      return this.v0 + v * (this.v1 - this.v0);
    }
  }

  private static final class Page {
    final int id;
    int x, y, shelfHeight;

    Page(int id) {
      this.id = id;
    }
  }

  private final boolean smooth;
  private final List<Page> pages = new ArrayList<>();
  private final Map<Image, Region> regions = new IdentityHashMap<>();

  CostumeAtlas(boolean smooth) {
    this.smooth = smooth;
  }

  /** @return where the costume lies, or null when it is not in the atlas (yet) */
  Region find(Image image) {
    return this.regions.get(image);
  }

  /** @return whether the costume belongs in the atlas: small enough */
  static boolean fits(Image image) {
    return image.getWidth() <= MAX_SIZE && image.getHeight() <= MAX_SIZE && image.getWidth() > 0
        && image.getHeight() > 0;
  }

  /**
   * Puts a costume into a page. Binds that page's texture: whoever was drawing has to draw what it
   * collected first.
   */
  Region add(Image image, int[] premultiplied) {
    int w = image.getWidth(), h = image.getHeight();
    int cellW = align(w + 2 * PAD), cellH = align(h + 2 * PAD);
    Page page = this.place(cellW, cellH);
    int cx = page.x, cy = page.y;
    page.x += cellW;
    page.shelfHeight = Math.max(page.shelfHeight, cellH);
    // the cell: the costume, its edge pixels repeated into the border (clamping, as at a texture's edge)
    int[] cell = new int[cellW * cellH];
    for (int y = 0; y < cellH; y++) {
      int sy = Math.max(0, Math.min(h - 1, y - PAD));
      for (int x = 0; x < cellW; x++) {
        int sx = Math.max(0, Math.min(w - 1, x - PAD));
        cell[y * cellW + x] = premultiplied[sy * w + sx];
      }
    }
    glBindTexture(GL_TEXTURE_2D, page.id);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
    glTexSubImage2D(GL_TEXTURE_2D, 0, cx, cy, cellW, cellH, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, cell);
    if (this.smooth) {
      glGenerateMipmap(GL_TEXTURE_2D);
    }
    Region region = new Region(page.id, (cx + PAD) / (double) PAGE, (cy + PAD) / (double) PAGE,
        (cx + PAD + w) / (double) PAGE, (cy + PAD + h) / (double) PAGE);
    this.regions.put(image, region);
    return region;
  }

  /** The page and shelf with room for a cell, a new shelf or a new page if needed. */
  private Page place(int cellW, int cellH) {
    for (Page page : this.pages) {
      if (page.x + cellW > PAGE) {
        page.y += page.shelfHeight;
        page.x = 0;
        page.shelfHeight = 0;
      }
      if (page.y + cellH <= PAGE) {
        return page;
      }
    }
    Page page = new Page(this.newTexture());
    this.pages.add(page);
    return page;
  }

  private int newTexture() {
    int id = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, id);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, PAGE, PAGE, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (int[]) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, this.smooth ? LEVELS : 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this.smooth ? GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this.smooth ? GL_LINEAR : GL_NEAREST);
    return id;
  }

  private static int align(int size) {
    return (size + PAD - 1) / PAD * PAD;
  }

  void delete() {
    for (Page page : this.pages) {
      glDeleteTextures(page.id);
    }
    this.pages.clear();
    this.regions.clear();
  }
}
