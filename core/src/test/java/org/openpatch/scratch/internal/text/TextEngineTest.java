package org.openpatch.scratch.internal.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Fonts, line breaking and layout without a window. */
class TextEngineTest {

  static final String SAMPLE = "Jörg fährt größere Straßen – „Hallo“ … 12,50 € 90° ÄÖÜ ß. "
      + "Supercalifragilisticexpialidocious-wordthatistoolong";

  /** Monospace: every codepoint 1 wide, no kerning. */
  static List<String> lines(String text, double maxWidth) {
    return LineBreaker.breakLines(text, maxWidth, (cps, from, to) -> to - from).stream()
        .map(cps -> new String(cps, 0, cps.length)).toList();
  }

  @Test
  void fontMetricsComeFromTheFile() {
    Font ubuntu = Font.defaultFont(), jersey = Font.pixelArtFont();
    assertEquals(1000, ubuntu.getUnitsPerEm());
    assertEquals(1, ubuntu.getPixelGrid());
    assertFalse(ubuntu.isPixelFont());
    assertEquals(1400, jersey.getUnitsPerEm());
    assertEquals(75, jersey.getPixelGrid());
    assertEquals(7.0, ubuntu.advance('a', 14), 1e-9, "monospace: 500 of 1000 units");
    assertEquals(ubuntu.lineHeight(14), (ubuntu.getAscent() - ubuntu.getDescent() + ubuntu.getLineGap()) * 0.014, 1e-9);
  }

  @Test
  void pixelFontSizesRoundToWholeFontPixels() {
    Font jersey = Font.pixelArtFont();
    assertEquals(18.6667, jersey.roundToPixelGrid(20), 1e-3);
    assertEquals(37.3333, jersey.roundToPixelGrid(30), 1e-3);
    assertEquals(18.6667, jersey.roundToPixelGrid(1), 1e-3, "at least one font pixel");
    assertEquals(14, Font.defaultFont().roundToPixelGrid(14), 1e-9, "not a pixel font");
    assertEquals(20, jersey.withPixelGrid(140).roundToPixelGrid(15), 1e-9, "1400 / 140 = a font pixel of 10");
  }

  @Test
  void aMissingFontIsReported() {
    assertThrows(IllegalArgumentException.class, () -> Font.load("no/such/font.ttf"));
  }

  @Test
  void linesBreakAtSpacesWhichBelongToNoLine() {
    assertEquals(List.of("aaa bbb", "ccc"), lines("aaa bbb ccc", 8));
    assertEquals(List.of("aaa", "bbb"), lines("aaa   bbb", 4));
    assertEquals(List.of("aaa bbb ccc"), lines("aaa bbb ccc", 0), "no wrapping");
  }

  @Test
  void newlinesAlwaysBreak() {
    assertEquals(List.of("a", "", "b"), lines("a\n\nb", 100));
    assertEquals(List.of(""), lines("", 10));
  }

  @Test
  void linesBreakAfterHyphens() {
    assertEquals(List.of("well-", "known"), lines("well-known", 7));
  }

  @Test
  void aLongWordBreaksBetweenLettersButAHyphenNeverStartsALine() {
    assertEquals(List.of("abcd", "efgh", "ij"), lines("abcdefghij", 4));
    // "abcd" fits, but "-" would start the next line: break one letter earlier
    assertEquals(List.of("abc", "d-ef"), lines("abcd-ef", 4));
    assertEquals(List.of("abc", "d-", "efgh"), lines("abcd-efgh", 4));
    assertEquals(List.of("a", "b"), lines("ab", 0.5), "at least one character per line");
  }

  @Test
  void layoutDoesNotDependOnAnythingButTextFontSizeAndWidth() {
    Font ubuntu = Font.defaultFont();
    TextLayout a = TextLayout.of(SAMPLE, ubuntu, 14, 220);
    assertSame(a, TextLayout.of(SAMPLE, ubuntu, 14, 220), "cached");
    assertEquals(4, a.lines().size());
    assertEquals("Supercalifragilisticexpialidoci", a.lines().get(2).text());
    assertEquals(a.lines().size() * ubuntu.lineHeight(14), a.height(), 1e-9);
    for (TextLayout.Line line : a.lines()) {
      assertTrue(line.width() <= 220, line.text());
    }
    TextLayout jersey = TextLayout.of(SAMPLE, Font.pixelArtFont(), 18.6667, 220);
    assertTrue(jersey.lines().stream().noneMatch(l -> l.text().startsWith("-")), "no line starts with a hyphen");
  }

  @Test
  void aGlyphMissingInTheFontComesFromTheFallback() {
    Font jersey = Font.pixelArtFont(), ubuntu = Font.defaultFont();
    int missing = -1;
    for (int cp = 0x100; cp < 0x2000; cp++) {
      if (!jersey.hasGlyph(cp) && ubuntu.hasGlyph(cp)) {
        missing = cp;
        break;
      }
    }
    assertTrue(missing > 0, "UbuntuMono covers something Jersey 10 does not");
    TextLayout layout = TextLayout.of("a" + Character.toString(missing), jersey, 18.6667, 0);
    assertSame(jersey, layout.lines().get(0).glyphs().get(0).font());
    assertSame(ubuntu, layout.lines().get(0).glyphs().get(1).font());
  }

  @Test
  void glyphsAreRasterisedAtDeviceSize() {
    Font ubuntu = Font.defaultFont();
    var small = GlyphRasterizer.rasterize(ubuntu, 'A', 14, 0, false);
    var large = GlyphRasterizer.rasterize(ubuntu, 'A', 28, 0, false);
    assertTrue(Math.abs(large.width() - 2 * small.width()) <= 2, small.width() + " vs " + large.width());
    assertTrue(small.offsetY() < 0, "above the baseline");
    var pixel = GlyphRasterizer.rasterize(Font.pixelArtFont(), 'A', 37.3333, 0.4, true);
    for (byte b : pixel.coverage()) {
      assertTrue(b == 0 || b == (byte) 255, "pixel art is not smoothed");
    }
    assertEquals(0, GlyphRasterizer.rasterize(ubuntu, ' ', 14, 0, false).width());
  }
}
