package org.openpatch.scratch.internal.text;

import java.io.IOException;

/**
 * TrueType fonts, ported from stb_truetype.h 1.26 (Sean Barrett, public domain) to Java:
 * character map, metrics, kerning ({@code kern} and GPOS pairs), glyph outlines and stb's version 2
 * rasteriser (exact area coverage). TrueType outlines only, no CFF. The arithmetic follows the C
 * code step by step in float, so bitmaps come out byte for byte as stb's do (checked against
 * LWJGL's build of it). Pure Java: the same on the desktop and in the browser.
 */
final class TrueType {

  private final byte[] data;
  private final int fontStart, numGlyphs, loca, head, glyf, hhea, hmtx, kern, gpos, indexMap, indexToLocFormat;

  static final byte VMOVE = 1, VLINE = 2, VCURVE = 3;

  /** A move, line or quadratic curve of an outline; {@code cx}, {@code cy} is a curve's control point. */
  record Vertex(byte type, short x, short y, short cx, short cy) {
    Vertex(byte type, int x, int y, int cx, int cy) {
      this(type, (short) x, (short) y, (short) cx, (short) cy);
    }
  }

  // ---- reading big-endian data --------------------------------------------------------------

  private static int u8(byte[] d, int p) {
    return d[p] & 0xff;
  }

  private static int s8(byte[] d, int p) {
    return d[p];
  }

  private static int u16(byte[] d, int p) {
    return (d[p] & 0xff) << 8 | d[p + 1] & 0xff;
  }

  private static int s16(byte[] d, int p) {
    return (short) u16(d, p);
  }

  /** Unsigned 32 bits; offsets in a font file fit an int. */
  private static int u32(byte[] d, int p) {
    return (d[p] & 0xff) << 24 | (d[p + 1] & 0xff) << 16 | (d[p + 2] & 0xff) << 8 | d[p + 3] & 0xff;
  }

  private static boolean tag(byte[] d, int p, String tag) {
    return d[p] == tag.charAt(0) && d[p + 1] == tag.charAt(1) && d[p + 2] == tag.charAt(2) && d[p + 3] == tag.charAt(3);
  }

  private static boolean tag4(byte[] d, int p, int a, int b, int c, int e) {
    return d[p] == a && d[p + 1] == b && d[p + 2] == c && d[p + 3] == e;
  }

  private static boolean isFont(byte[] d) {
    return tag4(d, 0, '1', 0, 0, 0) || tag(d, 0, "typ1") || tag(d, 0, "OTTO") || tag4(d, 0, 0, 1, 0, 0)
        || tag(d, 0, "true");
  }

  private static int findTable(byte[] d, int fontStart, String tag) {
    int tables = u16(d, fontStart + 4);
    int dir = fontStart + 12;
    for (int i = 0; i < tables; i++) {
      int loc = dir + 16 * i;
      if (tag(d, loc, tag)) {
        return u32(d, loc + 8);
      }
    }
    return 0;
  }

  /** Where the font at {@code index} starts: 0 for a single font, -1 when there is none. */
  static int fontOffset(byte[] d, int index) {
    if (isFont(d)) {
      return index == 0 ? 0 : -1;
    }
    if (tag(d, 0, "ttcf")) {
      int version = u32(d, 4);
      if (version == 0x00010000 || version == 0x00020000) {
        int n = u32(d, 8);
        if (index >= n) {
          return -1;
        }
        return u32(d, 12 + index * 4);
      }
    }
    return -1;
  }

  /** stb's InitFont. */
  TrueType(byte[] d, int fontStart, String name) throws IOException {
    this.data = d;
    this.fontStart = fontStart;
    int cmap = findTable(d, fontStart, "cmap");
    this.loca = findTable(d, fontStart, "loca");
    this.head = findTable(d, fontStart, "head");
    this.glyf = findTable(d, fontStart, "glyf");
    this.hhea = findTable(d, fontStart, "hhea");
    this.hmtx = findTable(d, fontStart, "hmtx");
    this.kern = findTable(d, fontStart, "kern");
    this.gpos = findTable(d, fontStart, "GPOS");
    if (cmap == 0 || this.head == 0 || this.hhea == 0 || this.hmtx == 0) {
      throw new IOException(name + " is not a TrueType or OpenType font.");
    }
    if (this.glyf == 0 || this.loca == 0) {
      throw new IOException(name + " has CFF outlines; only TrueType outlines are supported.");
    }
    int maxp = findTable(d, fontStart, "maxp");
    this.numGlyphs = maxp != 0 ? u16(d, maxp + 4) : 0xffff;
    int tables = u16(d, cmap + 2);
    int indexMap = 0;
    for (int i = 0; i < tables; i++) {
      int record = cmap + 4 + 8 * i;
      switch (u16(d, record)) {
        case 3 -> { // Microsoft
          int encoding = u16(d, record + 2);
          if (encoding == 1 || encoding == 10) {
            indexMap = cmap + u32(d, record + 4);
          }
        }
        case 0 -> indexMap = cmap + u32(d, record + 4); // Unicode
        default -> {}
      }
    }
    if (indexMap == 0) {
      throw new IOException(name + " has no Unicode character map.");
    }
    this.indexMap = indexMap;
    this.indexToLocFormat = u16(d, this.head + 50);
  }

  float scaleForMappingEmToPixels(float pixels) {
    int unitsPerEm = u16(this.data, this.head + 18);
    return pixels / unitsPerEm;
  }

  int findGlyphIndex(int codepoint) {
    byte[] d = this.data;
    int map = this.indexMap;
    int format = u16(d, map);
    if (format == 0) {
      int bytes = u16(d, map + 2);
      return codepoint < bytes - 6 ? u8(d, map + 6 + codepoint) : 0;
    } else if (format == 6) {
      int first = u16(d, map + 6), count = u16(d, map + 8);
      return codepoint >= first && codepoint < first + count ? u16(d, map + 10 + (codepoint - first) * 2) : 0;
    } else if (format == 4) {
      if (codepoint > 0xffff) {
        return 0;
      }
      int segCount = u16(d, map + 6) >> 1;
      int endCount = map + 14;
      // the first segment whose end is at or after the codepoint (stb searches binary; same result)
      int item = 0;
      while (item < segCount && u16(d, endCount + 2 * item) < codepoint) {
        item++;
      }
      if (item == segCount) {
        return 0;
      }
      int start = u16(d, map + 14 + segCount * 2 + 2 + 2 * item);
      int last = u16(d, endCount + 2 * item);
      if (codepoint < start || codepoint > last) {
        return 0;
      }
      int offset = u16(d, map + 14 + segCount * 6 + 2 + 2 * item);
      if (offset == 0) {
        return (codepoint + s16(d, map + 14 + segCount * 4 + 2 + 2 * item)) & 0xffff;
      }
      return u16(d, offset + (codepoint - start) * 2 + map + 14 + segCount * 6 + 2 + 2 * item);
    } else if (format == 12 || format == 13) {
      int groups = u32(d, map + 12);
      int low = 0, high = groups;
      while (low < high) {
        int mid = low + ((high - low) >> 1);
        long startChar = u32(d, map + 16 + mid * 12) & 0xffffffffL;
        long endChar = u32(d, map + 16 + mid * 12 + 4) & 0xffffffffL;
        if (codepoint < startChar) {
          high = mid;
        } else if (codepoint > endChar) {
          low = mid + 1;
        } else {
          int startGlyph = u32(d, map + 16 + mid * 12 + 8);
          return format == 12 ? (int) (startGlyph + codepoint - startChar) : startGlyph;
        }
      }
      return 0;
    }
    return 0;
  }

  /** The advance width of a glyph in font units. */
  int advanceWidth(int glyph) {
    byte[] d = this.data;
    int longMetrics = u16(d, this.hhea + 34);
    return glyph < longMetrics ? s16(d, this.hmtx + 4 * glyph) : s16(d, this.hmtx + 4 * (longMetrics - 1));
  }

  int kernAdvance(int g1, int g2) {
    if (this.gpos != 0) {
      return this.gposAdvance(g1, g2);
    } else if (this.kern != 0) {
      return this.kernTableAdvance(g1, g2);
    }
    return 0;
  }

  private int kernTableAdvance(int g1, int g2) {
    byte[] d = this.data;
    int k = this.kern;
    if (u16(d, k + 2) < 1 || u16(d, k + 8) != 1) {
      return 0;
    }
    int l = 0, r = u16(d, k + 10) - 1;
    long needle = (long) g1 << 16 | g2;
    while (l <= r) {
      int m = (l + r) >> 1;
      long straw = u32(d, k + 18 + m * 6) & 0xffffffffL;
      if (needle < straw) {
        r = m - 1;
      } else if (needle > straw) {
        l = m + 1;
      } else {
        return s16(d, k + 22 + m * 6);
      }
    }
    return 0;
  }

  private static int coverageIndex(byte[] d, int table, int glyph) {
    switch (u16(d, table)) {
      case 1 -> {
        int l = 0, r = u16(d, table + 2) - 1;
        while (l <= r) {
          int m = (l + r) >> 1;
          int straw = u16(d, table + 4 + 2 * m);
          if (glyph < straw) {
            r = m - 1;
          } else if (glyph > straw) {
            l = m + 1;
          } else {
            return m;
          }
        }
      }
      case 2 -> {
        int l = 0, r = u16(d, table + 2) - 1;
        while (l <= r) {
          int m = (l + r) >> 1;
          int record = table + 4 + 6 * m;
          int start = u16(d, record), end = u16(d, record + 2);
          if (glyph < start) {
            r = m - 1;
          } else if (glyph > end) {
            l = m + 1;
          } else {
            return u16(d, record + 4) + glyph - start;
          }
        }
      }
      default -> {
        return -1;
      }
    }
    return -1;
  }

  private static int glyphClass(byte[] d, int table, int glyph) {
    switch (u16(d, table)) {
      case 1 -> {
        int start = u16(d, table + 2), count = u16(d, table + 4);
        if (glyph >= start && glyph < start + count) {
          return u16(d, table + 6 + 2 * (glyph - start));
        }
      }
      case 2 -> {
        int l = 0, r = u16(d, table + 2) - 1;
        while (l <= r) {
          int m = (l + r) >> 1;
          int record = table + 4 + 6 * m;
          int start = u16(d, record), end = u16(d, record + 2);
          if (glyph < start) {
            r = m - 1;
          } else if (glyph > end) {
            l = m + 1;
          } else {
            return u16(d, record + 4);
          }
        }
      }
      default -> {
        return -1;
      }
    }
    return 0;
  }

  private int gposAdvance(int g1, int g2) {
    byte[] d = this.data;
    int gpos = this.gpos;
    if (u16(d, gpos) != 1 || u16(d, gpos + 2) != 0) {
      return 0;
    }
    int lookupList = gpos + u16(d, gpos + 8);
    int lookups = u16(d, lookupList);
    for (int i = 0; i < lookups; i++) {
      int lookup = lookupList + u16(d, lookupList + 2 + 2 * i);
      int subTables = u16(d, lookup + 4);
      if (u16(d, lookup) != 2) {
        continue; // pair adjustment only
      }
      for (int s = 0; s < subTables; s++) {
        int table = lookup + u16(d, lookup + 6 + 2 * s);
        int format = u16(d, table);
        int coverage = coverageIndex(d, table + u16(d, table + 2), g1);
        if (coverage == -1) {
          continue;
        }
        int valueFormat1 = u16(d, table + 4), valueFormat2 = u16(d, table + 6);
        if (valueFormat1 != 4 || valueFormat2 != 0) {
          return 0;
        }
        if (format == 1) {
          int pairSets = u16(d, table + 8);
          if (coverage >= pairSets) {
            return 0;
          }
          int pairValues = table + u16(d, table + 10 + 2 * coverage);
          int l = 0, r = u16(d, pairValues) - 1;
          while (l <= r) {
            int m = (l + r) >> 1;
            int pair = pairValues + 2 + 4 * m;
            int straw = u16(d, pair);
            if (g2 < straw) {
              r = m - 1;
            } else if (g2 > straw) {
              l = m + 1;
            } else {
              return s16(d, pair + 2);
            }
          }
        } else if (format == 2) {
          int class1 = glyphClass(d, table + u16(d, table + 8), g1);
          int class2 = glyphClass(d, table + u16(d, table + 10), g2);
          int class1Count = u16(d, table + 12), class2Count = u16(d, table + 14);
          if (class1 < 0 || class1 >= class1Count || class2 < 0 || class2 >= class2Count) {
            return 0;
          }
          return s16(d, table + 16 + 2 * (class1 * class2Count) + 2 * class2);
        } else {
          return 0;
        }
      }
    }
    return 0;
  }

  /** hhea's ascent, descent and line gap. */
  int[] verticalMetrics() {
    byte[] d = this.data;
    return new int[] {s16(d, this.hhea + 4), s16(d, this.hhea + 6), s16(d, this.hhea + 8)};
  }

  /** OS/2's typographic ascent, descent and line gap, or null without an OS/2 table. */
  int[] typoMetrics() {
    byte[] d = this.data;
    int os2 = findTable(d, this.fontStart, "OS/2");
    return os2 == 0 ? null : new int[] {s16(d, os2 + 68), s16(d, os2 + 70), s16(d, os2 + 72)};
  }

  /** Offset of a table, from the directory at the start of the font; -1 if absent. */
  int table(String tag) {
    int offset = findTable(this.data, this.fontStart, tag);
    return offset == 0 ? -1 : offset;
  }

  byte[] data() {
    return this.data;
  }

  // ---- glyph outlines -----------------------------------------------------------------------

  private int glyfOffset(int glyph) {
    if (glyph >= this.numGlyphs || this.indexToLocFormat >= 2) {
      return -1;
    }
    byte[] d = this.data;
    int g1, g2;
    if (this.indexToLocFormat == 0) {
      g1 = this.glyf + u16(d, this.loca + glyph * 2) * 2;
      g2 = this.glyf + u16(d, this.loca + glyph * 2 + 2) * 2;
    } else {
      g1 = this.glyf + u32(d, this.loca + glyph * 4);
      g2 = this.glyf + u32(d, this.loca + glyph * 4 + 4);
    }
    return g1 == g2 ? -1 : g1;
  }

  private int[] glyphBox(int glyph) {
    int g = this.glyfOffset(glyph);
    if (g < 0) {
      return null;
    }
    byte[] d = this.data;
    return new int[] {s16(d, g + 2), s16(d, g + 4), s16(d, g + 6), s16(d, g + 8)};
  }

  /** A glyph's outline; empty for a glyph without one (a space). */
  Vertex[] shape(int glyph) {
    byte[] d = this.data;
    int g = this.glyfOffset(glyph);
    if (g < 0) {
      return new Vertex[0];
    }
    int contours = s16(d, g);
    if (contours > 0) {
      int endPts = g + 10;
      int ins = u16(d, g + 10 + contours * 2);
      int points = g + 10 + contours * 2 + 2 + ins;
      int n = 1 + u16(d, endPts + contours * 2 - 2);
      int[] flags = new int[n], xs = new int[n], ys = new int[n];
      int flag = 0, flagCount = 0;
      for (int i = 0; i < n; i++) {
        if (flagCount == 0) {
          flag = u8(d, points++);
          if ((flag & 8) != 0) {
            flagCount = u8(d, points++);
          }
        } else {
          flagCount--;
        }
        flags[i] = flag;
      }
      int x = 0;
      for (int i = 0; i < n; i++) {
        int f = flags[i];
        if ((f & 2) != 0) {
          int dx = u8(d, points++);
          x += (f & 16) != 0 ? dx : -dx;
        } else if ((f & 16) == 0) {
          x = x + (short) (u8(d, points) * 256 + u8(d, points + 1));
          points += 2;
        }
        xs[i] = (short) x;
      }
      int y = 0;
      for (int i = 0; i < n; i++) {
        int f = flags[i];
        if ((f & 4) != 0) {
          int dy = u8(d, points++);
          y += (f & 32) != 0 ? dy : -dy;
        } else if ((f & 32) == 0) {
          y = y + (short) (u8(d, points) * 256 + u8(d, points + 1));
          points += 2;
        }
        ys[i] = (short) y;
      }
      java.util.List<Vertex> out = new java.util.ArrayList<>(n + 2 * contours);
      int sx = 0, sy = 0, cx = 0, cy = 0, scx = 0, scy = 0;
      boolean wasOff = false, startOff = false;
      int nextMove = 0, j = 0;
      for (int i = 0; i < n; i++) {
        int f = flags[i];
        x = xs[i];
        y = ys[i];
        if (nextMove == i) {
          if (i != 0) {
            closeShape(out, wasOff, startOff, sx, sy, scx, scy, cx, cy);
          }
          startOff = (f & 1) == 0;
          if (startOff) {
            scx = x;
            scy = y;
            if ((flags[i + 1] & 1) == 0) {
              sx = (x + xs[i + 1]) >> 1;
              sy = (y + ys[i + 1]) >> 1;
            } else {
              sx = xs[i + 1];
              sy = ys[i + 1];
              i++;
            }
          } else {
            sx = x;
            sy = y;
          }
          out.add(new Vertex(VMOVE, sx, sy, 0, 0));
          wasOff = false;
          nextMove = 1 + u16(d, endPts + j * 2);
          j++;
        } else if ((f & 1) == 0) {
          if (wasOff) {
            out.add(new Vertex(VCURVE, (cx + x) >> 1, (cy + y) >> 1, cx, cy));
          }
          cx = x;
          cy = y;
          wasOff = true;
        } else {
          if (wasOff) {
            out.add(new Vertex(VCURVE, x, y, cx, cy));
          } else {
            out.add(new Vertex(VLINE, x, y, 0, 0));
          }
          wasOff = false;
        }
      }
      closeShape(out, wasOff, startOff, sx, sy, scx, scy, cx, cy);
      return out.toArray(new Vertex[0]);
    } else if (contours < 0) {
      java.util.List<Vertex> out = new java.util.ArrayList<>();
      int comp = g + 10;
      boolean more = true;
      while (more) {
        float[] mtx = {1, 0, 0, 1, 0, 0};
        int flags = s16(d, comp);
        comp += 2;
        int index = s16(d, comp) & 0xffff;
        comp += 2;
        if ((flags & 2) != 0) {
          if ((flags & 1) != 0) {
            mtx[4] = s16(d, comp);
            comp += 2;
            mtx[5] = s16(d, comp);
            comp += 2;
          } else {
            mtx[4] = s8(d, comp);
            comp += 1;
            mtx[5] = s8(d, comp);
            comp += 1;
          }
        }
        if ((flags & (1 << 3)) != 0) {
          mtx[0] = mtx[3] = s16(d, comp) / 16384.0f;
          comp += 2;
          mtx[1] = mtx[2] = 0;
        } else if ((flags & (1 << 6)) != 0) {
          mtx[0] = s16(d, comp) / 16384.0f;
          comp += 2;
          mtx[1] = mtx[2] = 0;
          mtx[3] = s16(d, comp) / 16384.0f;
          comp += 2;
        } else if ((flags & (1 << 7)) != 0) {
          mtx[0] = s16(d, comp) / 16384.0f;
          comp += 2;
          mtx[1] = s16(d, comp) / 16384.0f;
          comp += 2;
          mtx[2] = s16(d, comp) / 16384.0f;
          comp += 2;
          mtx[3] = s16(d, comp) / 16384.0f;
          comp += 2;
        }
        float m = (float) Math.sqrt(mtx[0] * mtx[0] + mtx[1] * mtx[1]);
        float nn = (float) Math.sqrt(mtx[2] * mtx[2] + mtx[3] * mtx[3]);
        for (Vertex v : this.shape(index)) {
          int vx = v.x, vy = v.y;
          short x = (short) (int) (m * (mtx[0] * vx + mtx[2] * vy + mtx[4]));
          short y = (short) (int) (nn * (mtx[1] * vx + mtx[3] * vy + mtx[5]));
          vx = v.cx;
          vy = v.cy;
          short cx = (short) (int) (m * (mtx[0] * vx + mtx[2] * vy + mtx[4]));
          short cy = (short) (int) (nn * (mtx[1] * vx + mtx[3] * vy + mtx[5]));
          out.add(new Vertex(v.type, x, y, cx, cy));
        }
        more = (flags & (1 << 5)) != 0;
      }
      return out.toArray(new Vertex[0]);
    }
    return new Vertex[0];
  }

  private static void closeShape(java.util.List<Vertex> out, boolean wasOff, boolean startOff, int sx, int sy,
      int scx, int scy, int cx, int cy) {
    if (startOff) {
      if (wasOff) {
        out.add(new Vertex(VCURVE, (cx + scx) >> 1, (cy + scy) >> 1, cx, cy));
      }
      out.add(new Vertex(VCURVE, sx, sy, scx, scy));
    } else if (wasOff) {
      out.add(new Vertex(VCURVE, sx, sy, cx, cy));
    } else {
      out.add(new Vertex(VLINE, sx, sy, 0, 0));
    }
  }

  // ---- bitmaps ------------------------------------------------------------------------------

  /** Pixels a glyph touches at a scale and sub-pixel shift: x0, y0, x1, y1, y growing downwards. */
  int[] bitmapBox(int glyph, float scaleX, float scaleY, float shiftX, float shiftY) {
    int[] b = this.glyphBox(glyph);
    if (b == null) {
      return new int[4];
    }
    return new int[] {(int) Math.floor(b[0] * scaleX + shiftX), (int) Math.floor(-b[3] * scaleY + shiftY),
        (int) Math.ceil(b[2] * scaleX + shiftX), (int) Math.ceil(-b[1] * scaleY + shiftY)};
  }

  /** Coverage of a glyph into {@code out} (w × h, rows {@code stride} apart), as its bitmap box places it. */
  void makeBitmap(byte[] out, int w, int h, int stride, float scaleX, float scaleY, float shiftX, float shiftY,
      int glyph) {
    Vertex[] vertices = this.shape(glyph);
    int[] box = this.bitmapBox(glyph, scaleX, scaleY, shiftX, shiftY);
    if (w == 0 || h == 0) {
      return;
    }
    rasterize(out, w, h, stride, 0.35f, vertices, scaleX, scaleY, shiftX, shiftY, box[0], box[1]);
  }

  private static void rasterize(byte[] pixels, int w, int h, int stride, float flatness, Vertex[] vertices,
      float scaleX, float scaleY, float shiftX, float shiftY, int offX, int offY) {
    float scale = scaleX > scaleY ? scaleY : scaleX;
    float objFlatness = flatness / scale;
    float flatnessSquared = objFlatness * objFlatness;
    int contours = 0;
    for (Vertex v : vertices) {
      if (v.type == VMOVE) {
        contours++;
      }
    }
    if (contours == 0) {
      return;
    }
    // flatten the curves into closed polygons
    int[] lengths = new int[contours];
    FloatList pts = new FloatList();
    int n = -1, start = 0;
    float x = 0, y = 0;
    for (Vertex v : vertices) {
      switch (v.type) {
        case VMOVE -> {
          if (n >= 0) {
            lengths[n] = pts.size() - start;
          }
          n++;
          start = pts.size();
          x = v.x;
          y = v.y;
          pts.add(x, y);
        }
        case VLINE -> {
          x = v.x;
          y = v.y;
          pts.add(x, y);
        }
        case VCURVE -> {
          tesselate(pts, x, y, v.cx, v.cy, v.x, v.y, flatnessSquared, 0);
          x = v.x;
          y = v.y;
        }
        default -> {}
      }
    }
    lengths[n] = pts.size() - start;

    // the polygons' edges, top to bottom (y grows downwards: invert)
    float yScaleInv = -scaleY;
    int total = 0;
    for (int len : lengths) {
      total += len;
    }
    Edge[] edges = new Edge[total + 1];
    int e = 0, m = 0;
    for (int len : lengths) {
      int p = m;
      m += len;
      for (int k = 0, j = len - 1; k < len; j = k++) {
        float jy = pts.y(p + j), ky = pts.y(p + k);
        if (jy == ky) {
          continue;
        }
        int a = k, b = j;
        boolean invert = false;
        if (jy > ky) { // invert ? p[j].y > p[k].y : p[j].y < p[k].y
          invert = true;
          a = j;
          b = k;
        }
        Edge edge = new Edge();
        edge.invert = invert;
        edge.x0 = pts.x(p + a) * scaleX + shiftX;
        edge.y0 = (pts.y(p + a) * yScaleInv + shiftY) * 1;
        edge.x1 = pts.x(p + b) * scaleX + shiftX;
        edge.y1 = (pts.y(p + b) * yScaleInv + shiftY) * 1;
        edges[e++] = edge;
      }
    }
    edges[e] = new Edge();
    sortEdges(edges, e);
    rasterizeSorted(pixels, w, h, stride, edges, e, offX, offY);
  }

  private static void tesselate(FloatList pts, float x0, float y0, float x1, float y1, float x2, float y2,
      float flatnessSquared, int n) {
    float mx = (x0 + 2 * x1 + x2) / 4;
    float my = (y0 + 2 * y1 + y2) / 4;
    float dx = (x0 + x2) / 2 - mx;
    float dy = (y0 + y2) / 2 - my;
    if (n > 16) {
      return;
    }
    if (dx * dx + dy * dy > flatnessSquared) {
      tesselate(pts, x0, y0, (x0 + x1) / 2.0f, (y0 + y1) / 2.0f, mx, my, flatnessSquared, n + 1);
      tesselate(pts, mx, my, (x1 + x2) / 2.0f, (y1 + y2) / 2.0f, x2, y2, flatnessSquared, n + 1);
    } else {
      pts.add(x2, y2);
    }
  }

  private static final class Edge {
    float x0, y0, x1, y1;
    boolean invert;
  }

  private static final class Active {
    Active next;
    float fx, fdx, fdy, direction, sy, ey;
  }

  // stb's sort, quicksort then insertion sort: equal y0 keep stb's order, and with it the sums
  private static void sortEdges(Edge[] p, int n) {
    quicksort(p, 0, n);
    insertionSort(p, n);
  }

  private static boolean before(Edge a, Edge b) {
    return a.y0 < b.y0;
  }

  private static void insertionSort(Edge[] p, int n) {
    for (int i = 1; i < n; i++) {
      Edge t = p[i];
      int j = i;
      while (j > 0) {
        if (!before(t, p[j - 1])) {
          break;
        }
        p[j] = p[j - 1];
        j--;
      }
      if (i != j) {
        p[j] = t;
      }
    }
  }

  private static void quicksort(Edge[] a, int base, int n) {
    while (n > 12) {
      int m = n >> 1;
      boolean c01 = before(a[base], a[base + m]);
      boolean c12 = before(a[base + m], a[base + n - 1]);
      if (c01 != c12) {
        boolean c = before(a[base], a[base + n - 1]);
        int z = c == c12 ? 0 : n - 1;
        Edge t = a[base + z];
        a[base + z] = a[base + m];
        a[base + m] = t;
      }
      Edge t = a[base];
      a[base] = a[base + m];
      a[base + m] = t;
      int i = 1, j = n - 1;
      for (;;) {
        while (before(a[base + i], a[base])) {
          i++;
        }
        while (before(a[base], a[base + j])) {
          j--;
        }
        if (i >= j) {
          break;
        }
        t = a[base + i];
        a[base + i] = a[base + j];
        a[base + j] = t;
        i++;
        j--;
      }
      if (j < n - i) {
        quicksort(a, base, j);
        base = base + i;
        n = n - i;
      } else {
        quicksort(a, base + i, n - i);
        n = j;
      }
    }
  }

  private static void rasterizeSorted(byte[] pixels, int w, int h, int stride, Edge[] edges, int n, int offX,
      int offY) {
    Active active = null;
    float[] scanline = new float[w];
    float[] scanline2 = new float[w + 1];
    int y = offY;
    edges[n].y0 = (float) (offY + h) + 1;
    int e = 0;
    for (int j = 0; j < h; j++) {
      float top = y + 0.0f, bottom = y + 1.0f;
      java.util.Arrays.fill(scanline, 0);
      java.util.Arrays.fill(scanline2, 0);
      // drop the edges that end above this scanline
      Active prev = null;
      for (Active z = active; z != null; z = z.next) {
        if (z.ey <= top) {
          if (prev == null) {
            active = z.next;
          } else {
            prev.next = z.next;
          }
        } else {
          prev = z;
        }
      }
      // add the edges that start above its bottom, at the front
      while (edges[e].y0 <= bottom) {
        Edge edge = edges[e];
        if (edge.y0 != edge.y1) {
          Active z = newActive(edge, offX, top);
          if (j == 0 && offY != 0 && z.ey < top) {
            z.ey = top;
          }
          z.next = active;
          active = z;
        }
        e++;
      }
      if (active != null) {
        fillActive(scanline, scanline2, w, active, top);
      }
      float sum = 0;
      for (int i = 0; i < w; i++) {
        sum += scanline2[i];
        float k = scanline[i] + sum;
        k = Math.abs(k) * 255 + 0.5f;
        int v = (int) k;
        if (v > 255) {
          v = 255;
        }
        pixels[j * stride + i] = (byte) v;
      }
      for (Active z = active; z != null; z = z.next) {
        z.fx += z.fdx;
      }
      y++;
    }
  }

  private static Active newActive(Edge e, int offX, float start) {
    Active z = new Active();
    float dxdy = (e.x1 - e.x0) / (e.y1 - e.y0);
    z.fdx = dxdy;
    z.fdy = dxdy != 0.0f ? (1.0f / dxdy) : 0.0f;
    z.fx = e.x0 + dxdy * (start - e.y0);
    z.fx -= offX;
    z.direction = e.invert ? 1.0f : -1.0f;
    z.sy = e.y0;
    z.ey = e.y1;
    return z;
  }

  /** stb's handle_clipped_edge into {@code line[base + x]}. */
  private static void clipped(float[] line, int base, int x, Active e, float x0, float y0, float x1, float y1) {
    if (y0 == y1 || y0 > e.ey || y1 < e.sy) {
      return;
    }
    if (y0 < e.sy) {
      x0 += (x1 - x0) * (e.sy - y0) / (y1 - y0);
      y0 = e.sy;
    }
    if (y1 > e.ey) {
      x1 += (x1 - x0) * (e.ey - y1) / (y1 - y0);
      y1 = e.ey;
    }
    if (x0 <= x && x1 <= x) {
      line[base + x] += e.direction * (y1 - y0);
    } else if (x0 >= x + 1 && x1 >= x + 1) {
      // nothing
    } else {
      line[base + x] += e.direction * (y1 - y0) * (1 - ((x0 - x) + (x1 - x)) / 2);
    }
  }

  private static float trapezoid(float height, float tx0, float tx1, float bx0, float bx1) {
    return ((tx1 - tx0) + (bx1 - bx0)) / 2.0f * height;
  }

  private static float triangle(float height, float width) {
    return height * width / 2;
  }

  /** stb's fill_active_edges_new; {@code fill} is scanline2 with stb's +1 offset as base 1. */
  private static void fillActive(float[] scanline, float[] fill, int len, Active e, float top) {
    float bottom = top + 1;
    for (; e != null; e = e.next) {
      if (e.fdx == 0) {
        float x0 = e.fx;
        if (x0 < len) {
          if (x0 >= 0) {
            clipped(scanline, 0, (int) x0, e, x0, top, x0, bottom);
            clipped(fill, 0, (int) x0 + 1, e, x0, top, x0, bottom);
          } else {
            clipped(fill, 0, 0, e, x0, top, x0, bottom);
          }
        }
        continue;
      }
      float x0 = e.fx, dx = e.fdx, xb = x0 + dx, dy = e.fdy;
      float xTop, xBottom, sy0, sy1;
      if (e.sy > top) {
        xTop = x0 + dx * (e.sy - top);
        sy0 = e.sy;
      } else {
        xTop = x0;
        sy0 = top;
      }
      if (e.ey < bottom) {
        xBottom = x0 + dx * (e.ey - top);
        sy1 = e.ey;
      } else {
        xBottom = xb;
        sy1 = bottom;
      }
      if (xTop >= 0 && xBottom >= 0 && xTop < len && xBottom < len) {
        if ((int) xTop == (int) xBottom) {
          int x = (int) xTop;
          float height = (sy1 - sy0) * e.direction;
          scanline[x] += trapezoid(height, xTop, x + 1.0f, xBottom, x + 1.0f);
          fill[1 + x] += height;
        } else {
          if (xTop > xBottom) {
            sy0 = bottom - (sy0 - top);
            sy1 = bottom - (sy1 - top);
            float t = sy0;
            sy0 = sy1;
            sy1 = t;
            t = xBottom;
            xBottom = xTop;
            xTop = t;
            dx = -dx;
            dy = -dy;
            t = x0;
            x0 = xb;
            xb = t;
          }
          int x1 = (int) xTop, x2 = (int) xBottom;
          float yCrossing = top + dy * (x1 + 1 - x0);
          float yFinal = top + dy * (x2 - x0);
          if (yCrossing > bottom) {
            yCrossing = bottom;
          }
          float sign = e.direction;
          float area = sign * (yCrossing - sy0);
          scanline[x1] += triangle(area, x1 + 1 - xTop);
          if (yFinal > bottom) {
            int denom = x2 - (x1 + 1);
            yFinal = bottom;
            if (denom != 0) {
              dy = (yFinal - yCrossing) / denom;
            }
          }
          float step = sign * dy * 1;
          for (int x = x1 + 1; x < x2; x++) {
            scanline[x] += area + step / 2;
            area += step;
          }
          scanline[x2] += area + sign * trapezoid(sy1 - yFinal, (float) x2, x2 + 1.0f, xBottom, x2 + 1.0f);
          fill[1 + x2] += sign * (sy1 - sy0);
        }
      } else {
        for (int x = 0; x < len; x++) {
          float y0 = top;
          float x1 = (float) x;
          float x2 = (float) (x + 1);
          float x3 = xb;
          float y3 = bottom;
          float y1 = (x - x0) / dx + top;
          float y2 = (x + 1 - x0) / dx + top;
          if (x0 < x1 && x3 > x2) {
            clipped(scanline, 0, x, e, x0, y0, x1, y1);
            clipped(scanline, 0, x, e, x1, y1, x2, y2);
            clipped(scanline, 0, x, e, x2, y2, x3, y3);
          } else if (x3 < x1 && x0 > x2) {
            clipped(scanline, 0, x, e, x0, y0, x2, y2);
            clipped(scanline, 0, x, e, x2, y2, x1, y1);
            clipped(scanline, 0, x, e, x1, y1, x3, y3);
          } else if (x0 < x1 && x3 > x1) {
            clipped(scanline, 0, x, e, x0, y0, x1, y1);
            clipped(scanline, 0, x, e, x1, y1, x3, y3);
          } else if (x3 < x1 && x0 > x1) {
            clipped(scanline, 0, x, e, x0, y0, x1, y1);
            clipped(scanline, 0, x, e, x1, y1, x3, y3);
          } else if (x0 < x2 && x3 > x2) {
            clipped(scanline, 0, x, e, x0, y0, x2, y2);
            clipped(scanline, 0, x, e, x2, y2, x3, y3);
          } else if (x3 < x2 && x0 > x2) {
            clipped(scanline, 0, x, e, x0, y0, x2, y2);
            clipped(scanline, 0, x, e, x2, y2, x3, y3);
          } else {
            clipped(scanline, 0, x, e, x0, y0, x3, y3);
          }
        }
      }
    }
  }

  /** Points as x, y pairs. */
  private static final class FloatList {
    private float[] data = new float[64];
    private int size;

    void add(float x, float y) {
      if (2 * this.size + 2 > this.data.length) {
        this.data = java.util.Arrays.copyOf(this.data, this.data.length * 2);
      }
      this.data[2 * this.size] = x;
      this.data[2 * this.size + 1] = y;
      this.size++;
    }

    int size() {
      return this.size;
    }

    float x(int i) {
      return this.data[2 * i];
    }

    float y(int i) {
      return this.data[2 * i + 1];
    }
  }
}
