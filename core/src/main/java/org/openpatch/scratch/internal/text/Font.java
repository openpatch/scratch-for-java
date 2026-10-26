package org.openpatch.scratch.internal.text;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTVertex;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.Natives;
import org.openpatch.scratch.internal.image.Images;

/**
 * A TrueType or OpenType font read by stb_truetype. Metrics come from the file, unhinted, scaled
 * by size / units per em; sizes are in stage units. The font's bytes stay in a direct buffer as
 * long as the font lives, since stb_truetype reads its tables straight from it.
 *
 * <p>Thread-safe: layout runs on the game thread, rasterising on the window thread.
 */
public final class Font {

  static {
    Natives.init();
  }

  /** The default font in normal mode. */
  public static final String DEFAULT = "UbuntuMono-Regular.ttf";
  /** The default font in pixel-art mode. */
  public static final String PIXEL_ART = "Jersey10-Regular.ttf";

  private static final Map<String, Font> CACHE = new ConcurrentHashMap<>();

  private final String name;
  private final ByteBuffer data;
  final STBTTFontinfo info;
  private final int unitsPerEm, ascent, descent, lineGap;
  private final int pixelGrid;
  private final Font base; // the font this one is a view of (other grid), or null
  private final Map<Integer, Integer> advances;
  private final Map<Integer, Integer> glyphs;

  private Font(String name, byte[] bytes) throws IOException {
    this.name = name;
    this.base = null;
    this.data = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();
    this.info = STBTTFontinfo.malloc();
    if (!STBTruetype.stbtt_InitFont(this.info, this.data, STBTruetype.stbtt_GetFontOffsetForIndex(this.data, 0))) {
      this.info.free();
      MemoryUtil.memFree(this.data);
      throw new IOException(name + " is not a TrueType or OpenType font.");
    }
    // one em mapped to 1 px: its inverse is the units per em
    this.unitsPerEm = Math.round(1f / STBTruetype.stbtt_ScaleForMappingEmToPixels(this.info, 1));
    int[] m = this.verticalMetrics();
    this.ascent = m[0];
    this.descent = m[1];
    this.lineGap = m[2];
    this.pixelGrid = this.outlineGrid();
    this.advances = new ConcurrentHashMap<>();
    this.glyphs = new ConcurrentHashMap<>();
  }

  private Font(Font base, int pixelGrid) {
    this.name = base.name;
    this.base = base;
    this.data = base.data;
    this.info = base.info;
    this.unitsPerEm = base.unitsPerEm;
    this.ascent = base.ascent;
    this.descent = base.descent;
    this.lineGap = base.lineGap;
    this.pixelGrid = pixelGrid;
    this.advances = base.advances;
    this.glyphs = base.glyphs;
  }

  /**
   * The font at {@code path}: a file relative to the working directory, else on the classpath.
   * Loaded once.
   *
   * @throws IllegalArgumentException when there is no such font or it cannot be read
   */
  public static Font load(String path) {
    Font cached = CACHE.get(path);
    if (cached != null) {
      return cached;
    }
    byte[] bytes = Images.read(path);
    if (bytes == null) {
      throw new IllegalArgumentException("Font not found: " + path);
    }
    Font font;
    try {
      font = new Font(path, bytes);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
    Font raced = CACHE.putIfAbsent(path, font);
    if (raced != null) {
      font.close();
      return raced;
    }
    org.openpatch.scratch.internal.Assets.noteLoadedLate("font", path);
    return font;
  }

  /** A font from bytes, not cached; for fonts that do not come from a file. */
  public static Font fromBytes(byte[] data, String name) throws IOException {
    return new Font(name, data);
  }

  /** UbuntuMono, the default font in normal mode and the fallback for missing glyphs. */
  public static Font defaultFont() {
    return load(DEFAULT);
  }

  /** Jersey 10, the default font in pixel-art mode. */
  public static Font pixelArtFont() {
    return load(PIXEL_ART);
  }

  public String getName() {
    return this.name;
  }

  public int getUnitsPerEm() {
    return this.unitsPerEm;
  }

  /** Font units above the baseline. */
  public int getAscent() {
    return this.ascent;
  }

  /** Font units below the baseline, negative. */
  public int getDescent() {
    return this.descent;
  }

  public int getLineGap() {
    return this.lineGap;
  }

  /** Font units per font pixel; 1 for a font that is not a pixel font. */
  public int getPixelGrid() {
    return this.pixelGrid;
  }

  /** The same font with another pixel grid, for a pixel font whose outlines do not show it. */
  public Font withPixelGrid(int unitsPerPixel) {
    if (unitsPerPixel < 1) {
      throw new IllegalArgumentException("A pixel grid needs at least 1 unit per pixel.");
    }
    return new Font(this.base != null ? this.base : this, unitsPerPixel);
  }

  /** Whether it is a pixel font: its outlines sit on a grid coarser than the units. */
  public boolean isPixelFont() {
    return this.pixelGrid > 1;
  }

  /** Stage units per font unit at a size. */
  public double scaleFor(double size) {
    return size / this.unitsPerEm;
  }

  public boolean hasGlyph(int codepoint) {
    return this.glyph(codepoint) != 0;
  }

  int glyph(int codepoint) {
    return this.glyphs.computeIfAbsent(codepoint, cp -> STBTruetype.stbtt_FindGlyphIndex(this.info, cp));
  }

  /** The advance of a codepoint at a size, in stage units. */
  public double advance(int codepoint, double size) {
    int units = this.advances.computeIfAbsent(codepoint, cp -> {
      try (MemoryStack stack = MemoryStack.stackPush()) {
        IntBuffer advance = stack.mallocInt(1);
        STBTruetype.stbtt_GetGlyphHMetrics(this.info, this.glyph(cp), advance, null);
        return advance.get(0);
      }
    });
    return units * this.scaleFor(size);
  }

  /** The kerning between two codepoints at a size, in stage units ({@code kern} and GPOS pairs). */
  public double kerning(int left, int right, double size) {
    return STBTruetype.stbtt_GetGlyphKernAdvance(this.info, this.glyph(left), this.glyph(right))
        * this.scaleFor(size);
  }

  /** Distance between baselines at a size: ascent − descent + line gap. */
  public double lineHeight(double size) {
    return (this.ascent - this.descent + this.lineGap) * this.scaleFor(size);
  }

  public double ascent(double size) {
    return this.ascent * this.scaleFor(size);
  }

  /** How far letters reach below the baseline at a size, positive (Processing's textDescent). */
  public double descent(double size) {
    return -this.descent * this.scaleFor(size);
  }

  /**
   * The nearest size that is a whole number of font pixels (at least one), for pixel-art mode;
   * unchanged for a font that is not a pixel font. Jersey 10: 18.667, 37.333, …
   */
  public double roundToPixelGrid(double size) {
    if (!this.isPixelFont()) {
      return size;
    }
    double pixel = (double) this.unitsPerEm / this.pixelGrid;
    return Math.max(1, Math.round(size / pixel)) * pixel;
  }

  /** Frees the native memory; only for fonts made with {@link #fromBytes}. */
  public void close() {
    if (this.base == null) {
      this.info.free();
      MemoryUtil.memFree(this.data);
    }
  }

  /** hhea's metrics, or OS/2's typographic ones when the font asks for them (USE_TYPO_METRICS). */
  private int[] verticalMetrics() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer a = stack.mallocInt(1), d = stack.mallocInt(1), g = stack.mallocInt(1);
      int os2 = this.table("OS/2");
      boolean useTypo = os2 >= 0 && this.data.limit() > os2 + 64 && (u16(this.data, os2 + 62) & (1 << 7)) != 0;
      if (useTypo && STBTruetype.stbtt_GetFontVMetricsOS2(this.info, a, d, g)) {
        return new int[] {a.get(0), d.get(0), g.get(0)};
      }
      STBTruetype.stbtt_GetFontVMetrics(this.info, a, d, g);
      return new int[] {a.get(0), d.get(0), g.get(0)};
    }
  }

  /** The gcd of every outline coordinate of every glyph (Jersey 10: 75 of 1400 units). */
  private int outlineGrid() {
    int maxp = this.table("maxp");
    int glyphCount = maxp < 0 ? 0 : u16(this.data, maxp + 4);
    int grid = 0;
    for (int g = 0; g < glyphCount; g++) {
      STBTTVertex.Buffer shape = STBTruetype.stbtt_GetGlyphShape(this.info, g);
      if (shape == null) {
        continue;
      }
      for (STBTTVertex v : shape) {
        grid = gcd(grid, Math.abs(v.x()));
        grid = gcd(grid, Math.abs(v.y()));
        grid = gcd(grid, Math.abs(v.cx()));
        grid = gcd(grid, Math.abs(v.cy()));
      }
      STBTruetype.stbtt_FreeShape(this.info, shape);
      if (grid == 1) {
        break; // cannot get any finer
      }
    }
    return Math.max(1, grid);
  }

  private static int gcd(int a, int b) {
    while (b != 0) {
      int rest = a % b;
      a = b;
      b = rest;
    }
    return a;
  }

  /** Offset of a table, from the directory at the start of the file; -1 if absent. */
  private int table(String tag) {
    int offset = STBTruetype.stbtt_GetFontOffsetForIndex(this.data, 0);
    int tables = u16(this.data, offset + 4);
    for (int i = 0; i < tables; i++) {
      int record = offset + 12 + i * 16;
      if (this.data.get(record) == tag.charAt(0) && this.data.get(record + 1) == tag.charAt(1)
          && this.data.get(record + 2) == tag.charAt(2) && this.data.get(record + 3) == tag.charAt(3)) {
        return u16(this.data, record + 8) << 16 | u16(this.data, record + 10);
      }
    }
    return -1;
  }

  private static int u16(ByteBuffer b, int offset) {
    return (b.get(offset) & 0xff) << 8 | b.get(offset + 1) & 0xff;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
