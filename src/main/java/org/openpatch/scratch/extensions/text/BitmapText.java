package org.openpatch.scratch.extensions.text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.openpatch.scratch.TextAlign;
import org.openpatch.scratch.extensions.drawing.Drawing;
import org.openpatch.scratch.extensions.drawing.DrawingSprite;
import org.openpatch.scratch.internal.Image;

/**
 * Text made from image glyphs, such as font065.png for 'A'. Add it to a stage
 * like a sprite. Its position is the top of the first line, with each line
 * aligned left, centred or right. Spaces and line breaks do not load images.
 * Image data uses the library cache, so changing text does not create sprites
 * or reload files. Use ordinary Text for normal system/TrueType fonts.
 *
 *
   * @example.files BitmapTextExample.java
 */
public class BitmapText extends DrawingSprite {
  private String pattern;
  private String content = "";
  private TextAlign align = TextAlign.LEFT;
  private double spaceWidth = 22;
  private double letterSpacing;
  private double lineSpacing;
  private final Map<Integer, String> glyphs = new HashMap<>();

  /**
   * @param pattern image path with an integer format, e.g. "assets/font%03d.png"
   *
   * @example.files BitmapTextExample.java
   */
  public BitmapText(String pattern) { this.setFont(pattern); }

  /** @param text text to show, including spaces and newlines
   *
   * @example.files BitmapTextExample.java
   */
  public void showText(String text) { this.content = Objects.requireNonNull(text, "text"); }

  /**
   * @return the text being shown
   *
   * @example.files BitmapTextExample.java
   */
  public String getContent() { return this.content; }

  /** @param pattern glyph path with one integer format for the Unicode code point
   *
   * @example.files BitmapTextExample.java
   */
  public void setFont(String pattern) {
    Objects.requireNonNull(pattern, "font pattern");
    String conversions = pattern.replace("%%", "");
    var integer = java.util.regex.Pattern.compile("%(?:0?[1-9][0-9]*)?d").matcher(conversions);
    if (!integer.find() || integer.replaceFirst("").contains("%")) {
      throw new IllegalArgumentException("A bitmap font pattern needs an integer format, such as font%03d.png.");
    }
    // Validate the format now, before the next frame tries to load a glyph.
    try { String.format(Locale.ROOT, pattern, 65); }
    catch (java.util.IllegalFormatException invalid) {
      throw new IllegalArgumentException("Invalid bitmap font pattern: " + pattern, invalid);
    }
    this.pattern = pattern;
  }

  /**
   * Assigns an image to a special character, such as a controller button.
   * Overrides remain in effect when setFont() changes the ordinary glyphs.
   * @param character the character to replace
   * @param path its image or built-in costume name
   *
   * @example.files BitmapTextExample.java
   */
  public void addGlyph(char character, String path) {
    this.glyphs.put((int) character, Objects.requireNonNull(path, "glyph image"));
  }

  /** @param align LEFT, CENTER or RIGHT (DEFAULT means LEFT)
   *
   * @example.files BitmapTextExample.java
   */
  public void setAlign(TextAlign align) { this.align = Objects.requireNonNull(align, "alignment"); }

  /** @param width width of a space, in unscaled pixels
   *
   * @example.files BitmapTextExample.java
   */
  public void setSpaceWidth(double width) {
    finite(width);
    if (width < 0) throw new IllegalArgumentException("Space width cannot be negative.");
    this.spaceWidth = width;
  }

  /** @param spacing gap between characters; negative values allow italic glyphs to overlap
   *
   * @example.files BitmapTextExample.java
   */
  public void setLetterSpacing(double spacing) { finite(spacing); this.letterSpacing = spacing; }

  /** @param spacing extra pixels between lines
   *
   * @example.files BitmapTextExample.java
   */
  public void setLineSpacing(double spacing) {
    finite(spacing);
    if (spacing < 0) throw new IllegalArgumentException("Line spacing cannot be negative.");
    this.lineSpacing = spacing;
  }

  /**
   * @return the displayed text's width, including the sprite's size
   *
   * @example.files BitmapTextExample.java
   */
  @Override
  public int getWidth() {
    double width = 0;
    for (String line : this.content.split("\n", -1)) width = Math.max(width, this.lineWidth(line));
    return (int) Math.ceil(width * this.getSize() / 100);
  }

  /**
   * @return the displayed text's height, including line spacing and sprite size
   *
   * @example.files BitmapTextExample.java
   */
  @Override
  public int getHeight() {
    if (this.content.isEmpty()) return 0;
    String[] lines = this.content.split("\n", -1);
    double height = this.lineHeight() * lines.length + this.lineSpacing * (lines.length - 1);
    return (int) Math.ceil(height * this.getSize() / 100);
  }

  private String path(int codePoint) {
    String custom = this.glyphs.get(codePoint);
    return custom == null ? String.format(Locale.ROOT, this.pattern, codePoint) : custom;
  }

  private double width(int codePoint) {
    return codePoint == ' ' ? this.spaceWidth : Image.loadImageOrBuiltin(this.path(codePoint)).width;
  }

  private double lineWidth(String line) {
    int[] points = line.codePoints().toArray();
    double width = 0;
    for (int point : points) width += this.width(point);
    return Math.max(0, width + Math.max(0, points.length - 1) * this.letterSpacing);
  }

  private double lineHeight() {
    double height = 0;
    for (int point : this.content.codePoints().toArray()) {
      if (point != ' ' && point != '\n') height = Math.max(height, Image.loadImageOrBuiltin(this.path(point)).height);
    }
    return height;
  }

  /** @param drawing the current frame's canvas
   *
   * @example.files BitmapTextExample.java
   */
  @Override
  public void draw(Drawing drawing) {
    double y = 0;
    double lineStep = this.lineHeight() + this.lineSpacing;
    for (String line : this.content.split("\n", -1)) {
      double width = this.lineWidth(line);
      double x = this.align == TextAlign.CENTER ? -width / 2 : this.align == TextAlign.RIGHT ? -width : 0;
      for (int point : line.codePoints().toArray()) {
        double glyphWidth = this.width(point);
        if (point != ' ') {
          String path = this.path(point);
          drawing.image(path, x + glyphWidth / 2, y - drawing.imageHeight(path) / 2);
        }
        x += glyphWidth + this.letterSpacing;
      }
      y -= lineStep;
    }
  }

  private static void finite(double value) {
    if (!Double.isFinite(value)) throw new IllegalArgumentException("Bitmap font spacing must be finite.");
  }
}
