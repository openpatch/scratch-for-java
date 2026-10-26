package spike;

import java.util.ArrayList;
import java.util.List;

/**
 * Glyphs laid out in STAGE UNITS at the NOMINAL size: line breaks and the box
 * are the same at every scale, only the rasterisation multiplies by it.
 */
final class TextLayout {

  /** The font that actually has the glyph (the fallback for missing codepoints). */
  record Glyph(FontFile font, int codepoint, double x, double baselineY) {}

  record Line(String text, List<Glyph> glyphs, double width) {}

  final double size;
  final List<Line> lines;
  final double boxWidth, boxHeight;

  private TextLayout(double size, List<Line> lines, double boxWidth, double boxHeight) {
    this.size = size;
    this.lines = lines;
    this.boxWidth = boxWidth;
    this.boxHeight = boxHeight;
  }

  static TextLayout layout(FontFile font, FontFile fallback, String text, double size, double maxWidth) {
    float scale = font.emScale(size);
    float lineHeight = (font.ascent - font.descent + font.lineGap) * scale; // descent is negative
    List<Line> lines = new ArrayList<>();
    double boxWidth = 0;
    List<int[]> wrapped = wrap(font, fallback, text, size, maxWidth);
    for (int i = 0; i < wrapped.size(); i++) {
      int[] codepoints = wrapped.get(i);
      double baselineY = font.ascent * scale + i * lineHeight; // down from the box top
      List<Glyph> glyphs = new ArrayList<>();
      double x = 0;
      int previous = -1;
      FontFile previousFont = null;
      for (int codepoint : codepoints) {
        FontFile glyphFont = fontFor(font, fallback, codepoint);
        if (previous >= 0 && previousFont == glyphFont) {
          x += glyphFont.kern(previous, codepoint, size);
        }
        glyphs.add(new Glyph(glyphFont, codepoint, x, baselineY));
        x += glyphFont.advance(codepoint, size);
        previous = codepoint;
        previousFont = glyphFont;
      }
      String lineText = new String(codepoints, 0, codepoints.length);
      lines.add(new Line(lineText, glyphs, x));
      boxWidth = Math.max(boxWidth, x);
    }
    return new TextLayout(size, lines, boxWidth, lineHeight * lines.size());
  }

  /** Missing glyph (index 0): UbuntuMono if the font is another one, else glyph 0. */
  private static FontFile fontFor(FontFile font, FontFile fallback, int codepoint) {
    if (font.glyphIndex(codepoint) != 0) {
      return font;
    }
    if (font != fallback && fallback.glyphIndex(codepoint) != 0) {
      return fallback;
    }
    return font;
  }

  /**
   * Greedy wrapping: break at spaces and after hyphens, a word wider than the
   * line breaks between letters, and '\n' always breaks.
   */
  private static List<int[]> wrap(FontFile font, FontFile fallback, String text, double size, double maxWidth) {
    List<int[]> lines = new ArrayList<>();
    for (String paragraph : text.split("\n", -1)) {
      int[] codepoints = paragraph.codePoints().toArray();
      int start = 0;
      int end = 0; // chars [start, end) fit on the current line
      while (end < codepoints.length) {
        while (end < codepoints.length
            && width(font, fallback, codepoints, start, end + 1, size) <= maxWidth) {
          end++;
        }
        if (end == codepoints.length) {
          break;
        }
        if (end == start) {
          end++; // a single letter is wider than the line: it goes on one anyway
          lines.add(slice(codepoints, start, end));
        } else {
          int breakAfter = -1;
          for (int i = end - 1; i >= start; i--) {
            if (codepoints[i] == ' ' || codepoints[i] == '-') {
              breakAfter = i;
              break;
            }
          }
          if (breakAfter < 0) {
            lines.add(slice(codepoints, start, end)); // no break point: split between letters
          } else {
            lines.add(slice(codepoints, start, breakAfter + 1)); // the space or hyphen stays on the line
            end = breakAfter + 1; // the next line starts right after it
          }
        }
        start = end;
      }
      if (end > start || codepoints.length == 0) {
        lines.add(slice(codepoints, start, end)); // the rest, or an empty line from '\n'
      }
    }
    return lines;
  }

  /** Width of codepoints[from, to) in stage units, kerned within the fragment. */
  private static double width(FontFile font, FontFile fallback, int[] codepoints, int from, int to, double size) {
    double width = 0;
    int previous = -1;
    FontFile previousFont = null;
    for (int i = from; i < to; i++) {
      FontFile glyphFont = fontFor(font, fallback, codepoints[i]);
      if (previous >= 0 && previousFont == glyphFont) {
        width += glyphFont.kern(previous, codepoints[i], size);
      }
      width += glyphFont.advance(codepoints[i], size);
      previous = codepoints[i];
      previousFont = glyphFont;
    }
    return width;
  }

  private static int[] slice(int[] codepoints, int from, int to) {
    int[] line = new int[to - from];
    System.arraycopy(codepoints, from, line, 0, line.length);
    return line;
  }
}
