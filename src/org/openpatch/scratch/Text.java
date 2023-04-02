package org.openpatch.scratch;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;
import processing.core.PConstants;

public class Text implements Drawable {

  private float x;
  private float y;
  private float width;
  private boolean fullWidth;
  private float height;
  private long lifetime;
  private boolean hasLifetime;
  private String text;
  private String originalText;
  private Stage stage;

  private CopyOnWriteArrayList<Font> fonts = new CopyOnWriteArrayList<>();
  private int currentFont = 0;

  private String fontName;
  private int textSize;
  private boolean show;
  private TextStyle style;
  private Color textColor;
  private Color backgroundColor;
  private int textAlign = -1;

  public Text() {
    this("", 0, 0, 0);
    this.style = TextStyle.PLAIN;
    this.textAlign = TextAlign.CENTER;
  }

  public Text(String text, float x, float y, float width) {
    this.x = x;
    this.y = y;
    this.textSize = 16;
    this.originalText = text;
    this.width = width;
    this.show = false;
    this.backgroundColor = new Color(255, 255, 255);
    this.textColor = new Color(120, 120, 120);
    this.fontName = Font.defaultFontName;
    this.addFont(Font.defaultFontName, Font.defaultFontPath);
  }

  public Text(String text, float x, float y, float width, TextStyle style) {
    this(text, x, y, width);
    this.style = style;
  }

  public Text(
      String text,
      float x,
      float y,
      boolean fullWidth,
      TextStyle style) {
    this(text, x, y, Applet.getInstance().getWidth(), style);
    this.fullWidth = fullWidth;
  }

  public Text(Text t) {
    this.fullWidth = t.fullWidth;
    this.width = t.width;
    this.originalText = t.originalText;
    this.show = t.show;
    this.lifetime = t.lifetime;
    this.hasLifetime = t.hasLifetime;
    this.height = t.height;
    this.textSize = t.textSize;
    this.text = t.text;
    this.x = t.x;
    this.y = t.y;
    this.style = t.style;
    this.textColor = t.textColor;
    this.textAlign = t.textAlign;
    this.backgroundColor = t.backgroundColor;
    this.stage = t.stage;
    this.fonts = new CopyOnWriteArrayList<>();
    for (Font font : t.fonts) {
      this.fonts.add(new Font(font));
    }
  }

  public void addedToStage(Stage stage) {
    this.stage = stage;
    if (this.fullWidth) {
      this.width = Applet.getInstance().getWidth();
    }
  }

  public void removedFromStage(Stage stage) {
  }

  public void addFont(String name, String path) {
    for (Font font : this.fonts) {
      if (font.getName().equals(name)) {
        return;
      }
    }

    Font font = new Font(name, path);
    this.fonts.add(font);
  }

  public void switchFont(String name) {
    for (int i = 0; i < fonts.size(); i++) {
      Font font = fonts.get(i);
      if (font.getName().equals(name)) {
        this.currentFont = i;
        return;
      }
    }
  }

  public void nextFont() {
    this.currentFont = (this.currentFont + 1) % fonts.size();
  }

  public String getCurrentFontName() {
    if (fonts.size() == 0) {
      return null;
    }
    return this.fonts.get(this.currentFont).getName();
  }

  public int getCurrentFontIndex() {
    return this.currentFont;
  }

  /**
   * Sets the position of the text
   *
   * @param x a x coordinate
   * @param y a y coordinate
   */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setPosition(float x, float y) {
    this.setPosition(Math.round(x), Math.round(y));
  }

  public void setPosition(double x, double y) {
    this.setPosition((float) x, (float) y);
  }

  public void showText(String text) {
    this.show = true;
    this.hasLifetime = false;
    if (text == null || text.trim().length() == 0) {
      this.text = null;
    }
    this.originalText = text;
  }

  public void showText(String text, int millis) {
    showText(text);
    this.hasLifetime = true;
    this.lifetime = System.currentTimeMillis() + millis;
  }

  public void setStyle(TextStyle style) {
    this.style = style;
  }

  public void setBackgroundColor(int r, int g, int b) {
    this.backgroundColor = new Color(r, g, b);
  }

  public void setBackgroundColor(float h) {
    this.backgroundColor = new Color(h);
  }

  public void setBackgroundColor(Color c) {
    this.backgroundColor = c;
  }

  public void setTextColor(int r, int g, int b) {
    this.textColor = new Color(r, g, b);
  }

  public void setTextColor(float h) {
    this.textColor = new Color(h);
  }

  public void setTextColor(Color c) {
    this.textColor = c;
  }

  /**
   * This is copied from StackOverflow: https://stackoverflow.com/a/64582014
   * Wraps a source String into a series of lines having a maximum specified
   * length. The source is
   * wrapped at: spaces, horizontal tabs, system newLine characters, or a
   * specified newLine character
   * sequence. Existing newLine character sequences in the source string, whether
   * they be the system
   * newLine or the specified newLine, are honored. Existing whitespace (spaces
   * and horizontal tabs)
   * is preserved.
   * <p>
   * When wrapLongWords is true, words having a length greater than the specified
   * lineLength will be broken, the specified longWordBreak terminator appended,
   * and a new line initiated with the text of the specified longWordLinePrefix
   * string. The
   * position of the break will be unceremoniously chosen such that ineLength is
   * honored.
   * One use of longWordLinePrefix is to effect "hanging indents" by specifying a
   * series of
   * spaces for this parameter. This parameter can contain the lineFeed
   * character(s). Although
   * longWordLinePrefix can contain the horizontal tab character, the results are
   * not
   * guaranteed because no attempt is made to determine the quantity of character
   * positions occupied by a
   * horizontal tab.
   * </p>
   *
   * Example usage:
   *
   * <pre>
   * wrap("  A very long word is Abracadabra in my book", 11, "\n", true, "-", "  ");
   * </pre>
   *
   * returns (note the effect of the single-character lineFeed):
   *
   * <pre>
   *   A very
   * long word
   * is Abraca-
   *   dabra in
   * my book
   * </pre>
   *
   * Whereas, the following:
   *
   * <pre>
   * wrap("  A very long word is Abracadabra in my book", 11, null, true, null, "  ");
   * </pre>
   *
   * returns (due to the 2-character system linefeed):
   *
   * <pre>
   *   A very
   * long
   * word is A
   *   bracada
   *   bra in
   * my book
   * </pre>
   *
   * @param src                the String to be word wrapped, may be null
   * @param lineLength         the maximum line length, including the length of
   *                           newLineStr and, when
   *                           applicable, longWordLinePrefix. If the value is
   *                           insufficient to accommodate
   *                           these two parameters + 1 character, it will be
   *                           increased accordingly.
   * @param newLineStr         the string to insert for a new line, or
   *                           <code>null</code> to use the value
   *                           reported as the system line separator by the JVM
   * @param wrapLongWords      when false, words longer than wrapLength will not
   *                           be broken
   * @param longWordBreak      string with which to precede newLineStr on each
   *                           line of a broken word,
   *                           excepting the last line, or null if this feature is
   *                           not to be used
   * @param longWordLinePrefix string with which to prefix each line of a broken
   *                           word, subsequent
   *                           to the first line, or null if no prefix is to be
   *                           used
   * @return a line with newlines inserted, or <code>null</code> if src is null
   */
  public static String wrap(
      String src,
      int lineLength,
      String newLineStr,
      boolean wrapLongWords,
      String longWordBreak,
      String longWordLinePrefix) {
    // Trivial case
    if (src == null)
      return null;

    if (newLineStr == null)
      newLineStr = System.getProperty("line.separator");

    if (longWordBreak == null)
      longWordBreak = "";

    if (longWordLinePrefix == null)
      longWordLinePrefix = "";

    if (src.length() <= 0)
      return null;

    // Adjust maximum line length to accommodate the newLine string
    lineLength -= newLineStr.length();
    if (lineLength < 1)
      lineLength = 1;

    // Guard for long word break or prefix that would create an infinite loop
    if (wrapLongWords &&
        lineLength - longWordBreak.length() - longWordLinePrefix.length() < 1)
      lineLength += longWordBreak.length() + longWordLinePrefix.length();

    int remaining = lineLength, breakLength = longWordBreak.length();

    Matcher m = Pattern
        .compile(".+?[ \\t]|.+?(?:" + newLineStr + ")|.+?$")
        .matcher(src);

    StringBuilder cache = new StringBuilder();

    while (m.find()) {
      String word = m.group();

      // Breakup long word
      while (wrapLongWords && word.length() > lineLength) {
        cache
            .append(word, 0, remaining - breakLength)
            .append(longWordBreak)
            .append(newLineStr);
        word = longWordLinePrefix + word.substring(remaining - breakLength);
        remaining = lineLength;
      } // if

      // Linefeed if word exceeds remaining space
      if (word.length() > remaining) {
        cache.append(newLineStr).append(word);
        remaining = lineLength;
      } // if
      // Word fits in remaining space
      else
        cache.append(word);

      remaining -= word.length();
    } // while

    return cache.toString();
  } // wrap()

  public void setFont(String name) {
    this.fontName = name;
  }

  public String getFont() {
    return this.fontName;
  }

  public void setTextSize(int size) {
    this.textSize = size;
  }

  public int getTextSize() {
    return this.textSize;
  }

  public float getWidth() {
    return this.width;
  }

  public void setAlign(int align) {
    this.textAlign = align;
  }

  public void draw() {
    if (this.stage == null)
      return;
    if (!show || this.originalText == null)
      return;

    var textBuffer = Applet.getInstance();

    textBuffer.push();
    textBuffer.stroke(
        this.textColor.getRed(),
        this.textColor.getGreen(),
        this.textColor.getBlue());
    textBuffer.strokeWeight(2);
    textBuffer.fill(
        this.backgroundColor.getRed(),
        this.backgroundColor.getGreen(),
        this.backgroundColor.getBlue());
    textBuffer.rectMode(PApplet.CORNER);

    if (this.textAlign != TextAlign.DEFAULT) {
      textBuffer.textAlign(this.textAlign);
    } else {

      textBuffer.textAlign(PApplet.LEFT, PApplet.TOP);
    }
    textBuffer.textFont(this.fonts.get(this.currentFont).getFont(textSize));
    textBuffer.textSize(this.textSize);

    float cw = textBuffer.textWidth("w");

    float maxWidth = Math.min(
        this.width,
        Applet.getInstance().getWidth() - this.x);
    if (this.fullWidth || this.width <= 0) {
      maxWidth = Applet.getInstance().getWidth() - 16;
    }
    int lineLength = Math.round((maxWidth - 16) / cw);
    if (this.style != TextStyle.PLAIN) {
      this.text = wrap(originalText, lineLength, "\n", true, "-", " ");
    } else {
      this.text = originalText;
    }
    String[] lines = this.text.split("\n");

    float width = 0;
    if (this.fullWidth) {
      width = Applet.getInstance().getWidth();
    } else {
      // get minimum width
      for (String l : lines) {
        width = Math.max(textBuffer.textWidth(l), width);
      }
      width = Math.min(width + 16, this.width);
    }

    this.height = (this.textSize + 4) * lines.length + 16;
    textBuffer.stroke(
        this.textColor.getRed(),
        this.textColor.getGreen(),
        this.textColor.getBlue());
    if (this.style == TextStyle.PLAIN) {
      textBuffer.translate(this.x, this.y);
    } else if (this.style == TextStyle.BOX) {
      textBuffer.translate(this.x, this.y - height);
      textBuffer.rect(0, 0, width, this.height, 16, 16, 0, 0);
    } else {
      textBuffer.translate(this.x, this.y - height);
      textBuffer.rect(0, 0, width, this.height, 16, 16, 16, 16);
      if (this.style == TextStyle.SPEAK) {
        textBuffer.push();
        textBuffer.fill(
            this.backgroundColor.getRed(),
            this.backgroundColor.getGreen(),
            this.backgroundColor.getBlue());
        textBuffer.translate(10, height);
        textBuffer.triangle(0, 20, 0, 0, 20, 0);
        textBuffer.stroke(
            this.backgroundColor.getRed(),
            this.backgroundColor.getGreen(),
            this.backgroundColor.getBlue());
        textBuffer.strokeWeight(3);
        textBuffer.line(2, 0, 16, 0);
        textBuffer.pop();
      } else if (this.style == TextStyle.THINK) {
        textBuffer.circle(20, this.height, 10);
        textBuffer.circle(7, this.height + 7, 6);
        textBuffer.circle(0, this.height + 10, 4);
      }
    }
    textBuffer.fill(
        this.textColor.getRed(),
        this.textColor.getGreen(),
        this.textColor.getBlue());
    textBuffer.textLeading(this.textSize + 4);
    textBuffer.text(this.text, 8, 8);

    if (this.style == TextStyle.PLAIN && Applet.getInstance().isDebug()) {
      textBuffer.textSize(12);
      textBuffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
      textBuffer.textAlign(PConstants.CENTER);
      textBuffer.text("(" + x + ", " + y + ")", 0, -20);
    }
    textBuffer.pop();

    if (this.hasLifetime && this.lifetime < System.currentTimeMillis()) {
      this.show = false;
    }
  }
}
