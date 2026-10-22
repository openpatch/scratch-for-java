package spike;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTVertex;
import org.lwjgl.stb.STBTruetype;

/**
 * A TrueType file in a direct buffer, because stb_truetype reads the tables
 * straight out of it: the buffer must stay alive as long as the info does.
 */
final class FontFile implements AutoCloseable {

  final String name;
  final int unitsPerEm, ascent, descent, lineGap, numGlyphs, pixelGrid;
  /** Package-private: TextCheck rasterises through it, the spike has no API to hide. */
  final STBTTFontinfo info;

  private final ByteBuffer data;

  FontFile(String path, String name) throws IOException {
    this.name = name;
    byte[] bytes = Files.readAllBytes(Path.of(path));
    this.data = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();
    this.info = STBTTFontinfo.malloc();
    if (!STBTruetype.stbtt_InitFont(this.info, this.data, 0)) {
      throw new IOException(name + " is not a font stb_truetype can read: " + path);
    }
    // the em mapped to 1 px: its inverse is the units per em
    this.unitsPerEm = Math.round(1f / STBTruetype.stbtt_ScaleForMappingEmToPixels(this.info, 1));
    int[] ascent = new int[1], descent = new int[1], lineGap = new int[1];
    STBTruetype.stbtt_GetFontVMetrics(this.info, ascent, descent, lineGap);
    this.ascent = ascent[0];
    this.descent = descent[0]; // negative, below the baseline
    this.lineGap = lineGap[0];
    this.numGlyphs = u16(table("maxp") + 4);
    this.pixelGrid = this.grid();
  }

  /** Em to pixel scale for a nominal size, UNHINTED (stb never hints). */
  float emScale(double size) {
    return STBTruetype.stbtt_ScaleForMappingEmToPixels(this.info, (float) size);
  }

  int glyphIndex(int codepoint) {
    return STBTruetype.stbtt_FindGlyphIndex(this.info, codepoint);
  }

  /** Advance of a codepoint in stage units at the nominal size. */
  float advance(int codepoint, double size) {
    int[] advance = new int[1];
    STBTruetype.stbtt_GetGlyphHMetrics(this.info, this.glyphIndex(codepoint), advance, null);
    return advance[0] * this.emScale(size);
  }

  /** Kerning between two codepoints in stage units at the nominal size. */
  float kern(int first, int second, double size) {
    return STBTruetype.stbtt_GetCodepointKernAdvance(this.info, first, second) * this.emScale(size);
  }

  @Override
  public void close() {
    this.info.free();
    MemoryUtil.memFree(this.data);
  }

  /**
   * The pixel grid a pixel font snaps to: the gcd of every coordinate in every
   * glyph outline (Jersey 10: 75 of 1400 units per em, a 10 px font at em 18.6667).
   */
  private int grid() {
    int grid = 0;
    for (int glyph = 0; glyph < this.numGlyphs; glyph++) {
      STBTTVertex.Buffer shape = STBTruetype.stbtt_GetGlyphShape(this.info, glyph);
      if (shape == null) {
        continue;
      }
      for (STBTTVertex vertex : shape) {
        grid = gcd(grid, Math.abs(vertex.x()));
        grid = gcd(grid, Math.abs(vertex.y()));
        grid = gcd(grid, Math.abs(vertex.cx()));
        grid = gcd(grid, Math.abs(vertex.cy()));
      }
      STBTruetype.stbtt_FreeShape(this.info, shape);
    }
    return grid;
  }

  private static int gcd(int a, int b) {
    while (b != 0) {
      int rest = a % b;
      a = b;
      b = rest;
    }
    return a;
  }

  /** Offset of a table in the file, from the directory every TrueType starts with. */
  private int table(String tag) {
    int tables = u16(4);
    for (int i = 0; i < tables; i++) {
      int record = 12 + i * 16; // tag(4) checksum(4) offset(4) length(4), big-endian
      if (tag.equals(new String(new char[] {
          (char) this.data.get(record), (char) this.data.get(record + 1),
          (char) this.data.get(record + 2), (char) this.data.get(record + 3)}))) {
        return u32(record + 8);
      }
    }
    throw new IllegalStateException("no " + tag + " table in " + this.name);
  }

  private int u16(int offset) {
    return (this.data.get(offset) & 0xff) << 8 | this.data.get(offset + 1) & 0xff;
  }

  private int u32(int offset) {
    return this.u16(offset) << 16 | this.u16(offset + 2);
  }
}
