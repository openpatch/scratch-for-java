package org.openpatch.scratch.internal.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A text laid out in stage units at its nominal size: lines, glyph positions, the box. It never
 * depends on the window's size or scale, so bubble and box sizes are the same on every machine
 * and can be tested without a window; the renderer only multiplies by the scale. Alignment is
 * the renderer's business: lines start at x = 0.
 */
public final class TextLayout {

  /**
   * A glyph on a line.
   *
   * @param codepoint what to draw
   * @param font the font it is drawn with: the fallback when the text's font lacks it
   * @param x its pen position from the line's left edge
   */
  public record Glyph(int codepoint, Font font, double x) {}

  /**
   * A line.
   *
   * @param text its characters
   * @param width its width
   * @param baseline its baseline, measured down from the top of the box
   * @param glyphs its glyphs, left to right
   */
  public record Line(String text, double width, double baseline, List<Glyph> glyphs) {}

  private record Key(String text, Font font, double size, double maxWidth) {}

  private static final Map<Key, TextLayout> CACHE = Collections.synchronizedMap(
      new LinkedHashMap<>(64, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, TextLayout> eldest) {
          return this.size() > 256;
        }
      });

  private final List<Line> lines;
  private final double width, height, size;
  private final Font font;

  private TextLayout(List<Line> lines, double width, double height, double size, Font font) {
    this.lines = lines;
    this.width = width;
    this.height = height;
    this.size = size;
    this.font = font;
  }

  /**
   * The layout of {@code text}, from a small cache.
   *
   * @param maxWidth where lines wrap; 0 or less for no wrapping
   */
  public static TextLayout of(String text, Font font, double size, double maxWidth) {
    Key key = new Key(Objects.requireNonNull(text), font, size, maxWidth);
    TextLayout cached = CACHE.get(key);
    if (cached == null) {
      cached = layout(text, font, size, maxWidth);
      CACHE.put(key, cached);
    }
    return cached;
  }

  public List<Line> lines() {
    return this.lines;
  }

  /** The widest line. */
  public double width() {
    return this.width;
  }

  /** Lines × line height. */
  public double height() {
    return this.height;
  }

  public double size() {
    return this.size;
  }

  public Font font() {
    return this.font;
  }

  private static TextLayout layout(String text, Font font, double size, double maxWidth) {
    Font fallback = Font.defaultFont();
    LineBreaker.Measure measure = (cps, from, to) -> place(cps, from, to, font, fallback, size, null);
    double lineHeight = font.lineHeight(size);
    double ascent = font.ascent(size);
    List<Line> lines = new ArrayList<>();
    double widest = 0;
    for (int[] cps : LineBreaker.breakLines(text, maxWidth, measure)) {
      List<Glyph> glyphs = new ArrayList<>();
      double w = place(cps, 0, cps.length, font, fallback, size, glyphs);
      lines.add(new Line(new String(cps, 0, cps.length), w, ascent + lines.size() * lineHeight,
          List.copyOf(glyphs)));
      widest = Math.max(widest, w);
    }
    return new TextLayout(List.copyOf(lines), widest, lines.size() * lineHeight, size, font);
  }

  /** Lays codepoints out from x = 0; returns the width, and adds the glyphs if asked to. */
  private static double place(int[] cps, int from, int to, Font font, Font fallback, double size,
      List<Glyph> out) {
    double x = 0;
    int previous = -1;
    Font previousFont = null;
    for (int i = from; i < to; i++) {
      int cp = cps[i];
      Font f = !font.hasGlyph(cp) && font != fallback && fallback.hasGlyph(cp) ? fallback : font;
      if (previous >= 0 && previousFont == f) {
        x += f.kerning(previous, cp, size);
      }
      if (out != null) {
        out.add(new Glyph(cp, f, x));
      }
      x += f.advance(cp, size);
      previous = cp;
      previousFont = f;
    }
    return x;
  }
}
