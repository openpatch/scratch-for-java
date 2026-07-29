package org.openpatch.scratch;

import java.util.concurrent.CopyOnWriteArrayList;
import org.davidmoten.text.utils.WordWrap;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Font;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * The Text class represents a text object that can be displayed on the stage. It provides methods
 * to set the text content, position, size, style, and alignment. The text can be displayed in
 * various styles such as plain, speech bubble, or box. The text can be associated with a sprite and
 * displayed relative to the sprite's position. Example usage:
 *
 * <pre>{@code
 * Text text = new Text("Hello, World!", 100, 100, 200);
 * text.setStyle(TextStyle.SPEAK);
 * text.setBackgroundColor(255, 255, 255);
 * text.setTextColor(0, 0, 0);
 * text.setStrokeColor(0, 0, 0);
 * }</pre>
 *
 * @example.files TextConstructors.java
 */
public class Text {

  private double x;
  private double y;
  private double width;
  private double height;
  private long lifetime;
  private boolean hasLifetime;
  private String text;
  private String originalText;
  private Stage stage;
  private Sprite sprite;
  private boolean isUI;

  private CopyOnWriteArrayList<Font> fonts = new CopyOnWriteArrayList<>();
  private int currentFont = 0;

  private String fontName;
  private int textSize;
  private boolean show;
  /**
   * Plain from the start, so that a text drawn before anyone has chosen a style
   * has one. {@code new Text(words, x, y, width)} never set this, and draw()
   * switches on it: a text built that way threw a NullPointerException on every
   * frame, from inside the loading screen, so the program never got past it and
   * the stage stayed empty.
   */
  private TextStyle style = TextStyle.PLAIN;
  private Color textColor;
  private Color backgroundColor;
  private Color strokeColor;
  /**
   * Centred on its position, like everything else in a Scratch project: a
   * sprite put at (0, 60) has its middle there, and a text put at (0, 60) now
   * does too. The texts the library builds for itself - the stage's display and
   * ask bands, a sprite's speech bubble - are anchored by an edge instead and
   * say so where they are built.
   */
  private TextAlign textAlign = TextAlign.CENTER;
  /**
   * Set for the stage's display line. It is a band along the bottom edge rather
   * than a label: it reaches from side to side and squares off the two corners
   * that sit on the edge.
   */
  private boolean displayBand;

  /** SPEAK_BUBBLE_MAX_LIMIT is the maximum width of the speech bubble. */
  public static int SPEAK_BUBBLE_MAX_LIMIT = 330;

  /** SPEAK_BUBBLE_MIN_LIMIT is the minimum width of the speech bubble. */
  public static int SPEAK_BUBBLE_MIN_LIMIT = 80;

  private static String font = "UbuntuMono-Regular.ttf";
  private static int fontSize = 14;
  private static int[] fontSizes = { 14 };
  private static boolean smoothing = true;

  /**
   * Chooses the font all text is written in, and how big it is.
   *
   * <p>
   * Call it before the first stage is created. The font is loaded while the
   * window starts up, so choosing one later is not reliable.
   *
   * <pre>{@code
   * public static void main(String[] args) {
   *   Text.useFont("assets/Retro Gaming.ttf", 11);
   *   new MyStage();
   * }
   * }</pre>
   *
   * @param path the path to a .ttf or .otf file
   * @param size the size to write in
   *
   * @example.files TextUseFont.java
   */
  public static void useFont(String path, int size) {
    if (warnIfTooLate("Text.useFont(\"assets/Retro Gaming.ttf\", 11);")) {
      return;
    }
    if (path != null) {
      font = path;
    }
    if (size > 0) {
      fontSize = size;
      fontSizes = new int[] { size };
    }
    Font.reset();
  }

  /**
   * Chooses the font all text is written in, keeping the current size.
   *
   * @param path the path to a .ttf or .otf file
   */
  public static void useFont(String path) {
    useFont(path, fontSize);
  }

  /**
   * Prepares several sizes of the font in advance. Only needed when text is
   * shown at more than one size, so that every size looks sharp.
   *
   * @param sizes the sizes to prepare
   *
   * @example.files TextUseFontSizes.java
   */
  public static void useFontSizes(int... sizes) {
    if (warnIfTooLate("Text.useFontSizes(14, 20);")) {
      return;
    }
    if (sizes != null && sizes.length > 0) {
      fontSizes = sizes.clone();
      fontSize = sizes[0];
      Font.reset();
    }
  }

  /**
   * Chooses whether letters are smoothed. Turn it off for pixel fonts.
   *
   * @param smooth true to smooth the letters
   *
   * @example.files TextUseSmoothing.java
   */
  public static void useSmoothing(boolean smooth) {
    if (warnIfTooLate("Text.useSmoothing(false);")) {
      return;
    }
    smoothing = smooth;
    Font.reset();
  }

  /**
   * Says so, loudly, when a font setting is chosen after the window exists,
   * because the font is already loaded by then.
   */
  private static boolean warnIfTooLate(String example) {
    if (Window.getInstance() == null) {
      return false;
    }
    System.err.println("\n==============================================");
    System.err.println("WARNING: A font setting was chosen too late!");
    System.err.println("==============================================");
    System.err.println("\nThe window is already running and the font has been");
    System.err.println("loaded, so this may not take effect.");
    System.err.println("\nTip: Choose it before the first stage is created:");
    System.err.println("       " + example);
    System.err.println("       new MyStage();");
    System.err.println("==============================================\n");
    return true;
  }

  /**
   * Returns the font that text is written in by default.
   *
   * @return the path to the font file
   *
   * @example.files TextGetDefaultFont.java
   */
  public static String getDefaultFont() {
    return font;
  }

  /**
   * Returns the size text is written in by default.
   *
   * @return the font size
   *
   * @example.files TextGetDefaultFontSize.java
   */
  public static int getDefaultFontSize() {
    return fontSize;
  }

  /**
   * Returns the sizes prepared in advance.
   *
   * @return the font sizes
   *
   * @ignore-in-docs
   */
  public static int[] getDefaultFontSizes() {
    return fontSizes;
  }

  /**
   * Returns whether letters are smoothed.
   *
   * @return true if smoothing is on
   *
   * @ignore-in-docs
   */
  public static boolean isSmoothing() {
    return smoothing;
  }

  /**
   * Constructs a new Text object with default values. The text is initialized to an empty string,
   * positioned at (0, 0), with a default size of 0. The text style is set to plain and the text
   * is centred on its position, as every text is. Call {@link #setAlign(TextAlign)} for anything
   * else.
   */
  public Text() {
    this("", 0, 0, 0);
    this.style = TextStyle.PLAIN;
  }

  /**
   * Constructs a new Text object associated with a given Sprite.
   *
   * @param s the Sprite to associate with this Text object
   */
  public Text(Sprite s) {
    this(null, 0, 0, 0);
    this.sprite = s;
    this.style = TextStyle.SPEAK;
    // A bubble hangs off its sprite by the tail in its bottom left corner, so it
    // grows to the right rather than around the point it is pinned to.
    this.textAlign = TextAlign.LEFT;
  }

  /**
   * Constructs a new Text object with the specified text, position, and width.
   *
   * @param text the text to be displayed
   * @param x the x-coordinate of the text's position
   * @param y the y-coordinate of the text's position
   * @param width the width of the text
   */
  public Text(String text, double x, double y, double width) {
    this.x = x;
    this.y = y;
    this.textSize = fontSize;
    this.originalText = text;
    this.width = width;
    // A text built with words shows them. It used to wait for a showText(),
    // so `new Text("Hello World", 0, 0, 400)` put nothing on the stage - which
    // is how the documentation introduces the class. A text built with nothing
    // to say still stays hidden: that is a speech bubble waiting for say(), or
    // the stage's own display line waiting for display().
    this.show = text != null && !text.isBlank();
    this.backgroundColor = new Color(255, 255, 255);
    this.textColor = new Color(120, 120, 120);
    this.strokeColor = new Color(218, 218, 218);
    this.fontName = "default";
    this.addFont("default", font);
  }

  /**
   * Constructs a new Text object with the specified text, position, width, and style.
   *
   * @param text the text content to be displayed
   * @param x the x-coordinate of the text's position
   * @param y the y-coordinate of the text's position
   * @param width the width of the text area
   * @param style the style to be applied to the text
   */
  public Text(String text, double x, double y, double width, TextStyle style) {
    this(text, x, y, width);
    this.style = style;
  }

  /**
   * Copy constructor for the Text class. Creates a new Text object by copying the properties of the
   * given Text object.
   *
   * @param t the Text object to copy
   */
  public Text(Text t) {
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
    this.strokeColor = t.strokeColor;
    this.stage = t.stage;
    this.fonts = new CopyOnWriteArrayList<>();
    for (Font font : t.fonts) {
      this.fonts.add(new Font(font));
    }
  }

  /** @ignore-in-docs The plumbing that calls whenAddedToStage(), which is the one to override. */
  public void addedToStage(Stage stage) {
    this.stage = stage;
    this.whenAddedToStage();
    this.whenAddedToStage(stage);
  }

  /** @ignore-in-docs The plumbing that calls whenRemovedFromStage(), which is the one to override. */
  public void removedFromStage(Stage stage) {
    this.stage = null;
    this.whenRemovedFromStage();
    this.whenRemovedFromStage(stage);
  }

  /**
   * This method is called when the object is added to the stage. Override this method to define
   * custom behavior when the object is added to the stage.
   *
   * @example.files TextWhenAddedToStage.java
   */
  public void whenAddedToStage() {}

  /**
   * This method is called when the text is added to the stage. Override this method to define
   * custom behavior when the text.
   *
   * @param stage The stage to which the text is added.
   */
  public void whenAddedToStage(Stage stage) {}

  /**
   * This method is called when the object is removed from the stage. Override this method to define
   * custom behavior that should occur when the object is no longer part of the stage.
   *
   * @example.files TextWhenRemovedFromStage.java
   */
  public void whenRemovedFromStage() {}

  /**
   * This method is called when the text is removed from the stage. Override this method to define
   * custom behavior that should occur.
   *
   * @param stage The stage from which the text is removed.
   */
  public void whenRemovedFromStage(Stage stage) {}

  /**
   * Removes this object from its current stage if it is associated with one. If the object is not
   * associated with any stage, this method does nothing.
   *
   * @example.files TextRemove.java
   */
  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  /**
   * Retrieves the current stage associated with this object.
   *
   * @return the current stage
   *
   * @example.files TextGetStage.java
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * Adds a new font to the list of fonts if it does not already exist.
   *
   * @param name the name of the font to be added
   * @param path the path to the font file
   *
   * @example.files TextAddFont.java
   */
  public void addFont(String name, String path) {
    for (Font font : this.fonts) {
      if (font.getName().equals(name)) {
        return;
      }
    }

    Font font = new Font(name, path);
    this.fonts.add(font);
  }

  /**
   * Switches the current font to the font with the specified name.
   *
   * @param name the name of the font to switch to
   *
   * @example.files TextSwitchFont.java
   */
  public void switchFont(String name) {
    for (int i = 0; i < this.fonts.size(); i++) {
      Font font = this.fonts.get(i);
      if (font.getName().equals(name)) {
        this.currentFont = i;
        return;
      }
    }
  }

  /**
   * Advances to the next font in the list of available fonts. The current font index is incremented
   * by one and wraps around to the beginning of the list if it exceeds the number of available
   * fonts.
   *
   * @example.files TextNextFont.java
   */
  public void nextFont() {
    this.currentFont = (this.currentFont + 1) % this.fonts.size();
  }

  /**
   * Retrieves the name of the current font.
   *
   * @return the name of the current font, or {@code null} if no fonts are available.
   *
   * @example.files TextGetCurrentFontName.java
   */
  public String getCurrentFontName() {
    if (this.fonts.size() == 0) {
      return null;
    }
    return this.fonts.get(this.currentFont).getName();
  }

  /**
   * Returns the index of the current font.
   *
   * @return the index of the current font
   *
   * @example.files TextGetCurrentFontIndex.java
   */
  public int getCurrentFontIndex() {
    return this.currentFont;
  }

  /**
   * Sets the position of the text
   *
   * @param x a x coordinate
   * @param y a y coordinate
   *
   * @example.files TextSetPosition.java
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Sets the position of the text object using a Vector2 object.
   *
   * @param v the Vector2 object containing the x and y coordinates
   */
  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  /**
   * Retrieves the current position as a Vector2 object.
   *
   * @return a Vector2 object representing the current x and y coordinates.
   *
   * @example.files TextGetPosition.java
   */
  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  /**
   * Sets the x-coordinate for this object.
   *
   * @param x the new x-coordinate value
   *
   * @example.files TextSetX.java
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Returns the x-coordinate of this object.
   *
   * @return the x-coordinate as a double
   *
   * @example.files TextGetX.java
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the Y coordinate of the text.
   *
   * @param y the new Y coordinate value
   *
   * @example.files TextSetY.java
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Returns the y-coordinate of this object.
   *
   * @return the y-coordinate as a double
   *
   * @example.files TextGetY.java
   */
  public double getY() {
    return this.y;
  }

  /**
   * Displays the specified text.
   *
   * @param text The text to be displayed. If the text is null or empty, the text will be set to
   *     null.
   *
   * @example.files TextShowText.java
   */
  public void showText(String text) {
    this.show = true;
    this.hasLifetime = false;
    if (text == null || text.trim().length() == 0) {
      this.text = null;
    }
    this.originalText = text;
  }

  /**
   * Displays the specified text for a given duration.
   *
   * @param text The text to be displayed.
   * @param millis The duration in milliseconds for which the text should be displayed.
   */
  public void showText(String text, int millis) {
    this.showText(text);
    this.hasLifetime = true;
    this.lifetime = System.currentTimeMillis() + millis;
  }

  /**
   * Sets the style of the text.
   *
   * @param style the TextStyle to be applied to the text
   *
   * @example.files TextSetStyle.java
   */
  public void setStyle(TextStyle style) {
    this.style = style;
  }

  /**
   * Returns the style the text is drawn in.
   *
   * @return the style, never null
   *
   * @example.files TextGetStyle.java
   */
  public TextStyle getStyle() {
    return this.style;
  }

  /**
   * Sets the background color using the specified RGB values.
   *
   * @param r the red component of the color (0-255)
   * @param g the green component of the color (0-255)
   * @param b the blue component of the color (0-255)
   *
   * @example.files TextSetBackgroundColor.java
   */
  public void setBackgroundColor(int r, int g, int b) {
    this.backgroundColor = new Color(r, g, b);
  }

  /**
   * Sets the background color using the specified hue value.
   *
   * @param h the hue value for the background color
   */
  public void setBackgroundColor(double h) {
    this.backgroundColor = new Color(h);
  }

  /**
   * Sets the background color of the text.
   *
   * @param c the new background color to be set
   */
  public void setBackgroundColor(Color c) {
    this.backgroundColor = c;
  }

  /**
   * Sets the text color using RGB values.
   *
   * @param r the red component of the color (0-255)
   * @param g the green component of the color (0-255)
   * @param b the blue component of the color (0-255)
   *
   * @example.files TextSetTextColor.java
   */
  public void setTextColor(int r, int g, int b) {
    this.textColor = new Color(r, g, b);
  }

  /**
   * Sets the text color using the specified hue value.
   *
   * @param h the hue value for the text color
   */
  public void setTextColor(double h) {
    this.textColor = new Color(h);
  }

  /**
   * Sets the color of the text.
   *
   * @param c the new color to be set for the text
   */
  public void setTextColor(Color c) {
    this.textColor = c;
  }

  /**
   * Sets the stroke color using the specified RGB values.
   *
   * @param r the red component of the color (0-255)
   * @param g the green component of the color (0-255)
   * @param b the blue component of the color (0-255)
   *
   * @example.files TextSetStrokeColor.java
   */
  public void setStrokeColor(int r, int g, int b) {
    this.strokeColor = new Color(r, g, b);
  }

  /**
   * Sets the stroke color using the specified hue value.
   *
   * @param h the hue value for the stroke color
   */
  public void setStrokeColor(double h) {
    this.strokeColor = new Color(h);
  }

  /**
   * Sets the stroke color for the text.
   *
   * @param c the color to set as the stroke color
   */
  public void setStrokeColor(Color c) {
    this.strokeColor = c;
  }

  /**
   * Sets the font name for the text.
   *
   * @param name the name of the font to be set
   *
   * @example.files TextSetFont.java
   */
  public void setFont(String name) {
    this.fontName = name;
  }

  /**
   * Retrieves the name of the font.
   *
   * @return the name of the font as a String.
   *
   * @example.files TextGetFont.java
   */
  public String getFont() {
    return this.fontName;
  }

  /**
   * Sets the size of the text.
   *
   * @param size the new size of the text
   *
   * @example.files TextSetTextSize.java
   */
  public void setTextSize(int size) {
    this.textSize = size;
  }

  /**
   * Returns the size of the text.
   *
   * @return the size of the text as an integer
   *
   * @example.files TextGetTextSize.java
   */
  public int getTextSize() {
    return this.textSize;
  }

  /**
   * Returns the width of the text.
   *
   * @return the width of the text as a double
   *
   * @example.files TextGetWidth.java
   */
  public double getWidth() {
    return this.width;
  }

  /**
   * Sets the width of the text.
   *
   * @param width the new width to set
   *
   * @example.files TextSetWidth.java
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Sets the alignment of the text.
   *
   * @param align where the text should sit relative to its position
   *
   * @example.files TextSetAlign.java
   */
  public void setAlign(TextAlign align) {
    this.textAlign = align == null ? TextAlign.DEFAULT : align;
  }

  /**
   * Returns where the text sits relative to its position.
   *
   * @return the alignment
   *
   * @example.files TextGetAlign.java
   */
  public TextAlign getAlign() {
    return this.textAlign;
  }

  /**
   * Sets the UI status of this object.
   *
   * @param isUI a boolean indicating whether this object is part of the UI
   *
   * @example.files TextSetIsUI.java
   */
  public void setIsUI(boolean isUI) {
    this.isUI = isUI;
  }

  /**
   * Checks if the current instance is a UI element.
   *
   * @return {@code true} if this instance is a UI element, {@code false} otherwise.
   *
   * @example.files TextIsUI.java
   */
  public boolean isUI() {
    return this.isUI;
  }

  private String[] wrap(String text, double maxWidth, PGraphics buffer) {
    return WordWrap.from(text)
        .maxWidth(maxWidth)
        .breakWords(true)
        .stringWidth(s -> buffer.textWidth(s.toString()))
        .wrap()
        .split("\n");
  }

  /**
   * Draws this text as the band the stage writes display() into: as wide as the
   * width it was given rather than as wide as its words, and rounded only on the
   * two corners that are not against the edge of the stage.
   */
  void asDisplayBand() {
    this.displayBand = true;
  }

  /**
   * How far a box of this width has to be moved so that the position it was
   * given ends up on the side {@link #setAlign} asked for: its left edge by
   * default, its middle when centred, its right edge when right-aligned.
   */
  private double alignOffset(double boxWidth) {
    switch (this.textAlign) {
      case CENTER:
        return -boxWidth / 2;
      case RIGHT:
        return -boxWidth;
      default:
        return 0;
    }
  }

  /**
   * Draws the wrapped text inside a box of the given width, on the side the
   * alignment asks for. The buffer is already aligned, so this only has to put
   * the anchor on the correct edge of the box.
   */
  private void drawAlignedText(PGraphics buffer, double boxWidth) {
    buffer.textLeading(this.textSize + 4);
    var x = 8.0;
    if (this.textAlign == TextAlign.CENTER) {
      x = boxWidth / 2;
    } else if (this.textAlign == TextAlign.RIGHT) {
      x = boxWidth - 8;
    }
    buffer.text(this.text, (float) x, 8);
  }

  /** The width of the longest of the given lines, in the buffer's current font. */
  private double longestLineWidth(String[] lines, PGraphics buffer) {
    var longest = 0.0;
    for (var line : lines) {
      var lineWidth = buffer.textWidth(line);
      if (lineWidth > longest) {
        longest = lineWidth;
      }
    }
    return longest;
  }

  private void drawBubble(PGraphics buffer) {
    // A sprite's bubble wraps at the width a speech bubble is allowed to be; a
    // text of its own wraps where it was told to, like the other styles.
    var wrapWidth = this.sprite == null && this.width > 0
        ? this.width
        : SPEAK_BUBBLE_MAX_LIMIT;
    var lines = this.wrap(this.originalText, wrapWidth, buffer);
    var maxLineWidth = Math.max(longestLineWidth(lines, buffer), SPEAK_BUBBLE_MIN_LIMIT);

    // A sprite's bubble hangs off the top right corner of its hitbox rather
    // than off its costume. A costume is often drawn into a canvas bigger than
    // what is painted on it, and a bubble placed by the canvas floats away from
    // the sprite it belongs to; the hitbox is the sprite as it looks.
    //
    // A text that belongs to no sprite keeps the position it was given, the
    // same way the other styles do, and the bubble grows up and to the right
    // of it.
    if (this.sprite != null) {
      var bounds = this.sprite.getHitbox().getBounds();
      this.x = bounds.x() + bounds.width();
      // Hitboxes are built with y pointing down, the way the screen does.
      this.y = -bounds.y();
    }

    // The bubble is as wide as the longest line it holds. Drawing used to write
    // that back into the text's own width, so a box drawn after a bubble came
    // out the size of the bubble and setWidth() was quietly forgotten.
    var bubbleWidth = maxLineWidth + 16;
    var bubbleHeight = (this.textSize + 4) * lines.length + 16;
    this.height = bubbleHeight;
    this.text = String.join("\n", lines);
    buffer.rectMode(PConstants.CORNER);
    var mirror = false;
    buffer.translate(
        (float) (this.x + alignOffset(bubbleWidth)), (float) (-this.y - bubbleHeight));
    buffer.stroke(
        (float) this.strokeColor.getRed(),
        (float) this.strokeColor.getGreen(),
        (float) this.strokeColor.getBlue());
    buffer.fill(
        (float) this.backgroundColor.getRed(),
        (float) this.backgroundColor.getGreen(),
        (float) this.backgroundColor.getBlue());
    buffer.rect(0, 0, (float) bubbleWidth, (float) bubbleHeight, 16, 16, 16, 16);
    if (this.style == TextStyle.SPEAK) {
      buffer.push();
      buffer.fill(
          (float) this.backgroundColor.getRed(),
          (float) this.backgroundColor.getGreen(),
          (float) this.backgroundColor.getBlue());
      if (mirror) {
        buffer.translate((float) bubbleWidth - 40, (float) bubbleHeight);
        buffer.triangle(20, 0, 0, 0, 20, 20);
      } else {
        buffer.translate(10, (float) bubbleHeight);
        buffer.triangle(0, 20, 0, 0, 20, 0);
      }
      buffer.stroke(
          (float) this.backgroundColor.getRed(),
          (float) this.backgroundColor.getGreen(),
          (float) this.backgroundColor.getBlue());
      buffer.strokeWeight(3);
      buffer.line(2, 0, 16, 0);
      buffer.pop();
    } else if (this.style == TextStyle.THINK) {
      buffer.push();
      if (mirror) {
        buffer.translate((float) bubbleWidth - 10, 0);
        buffer.circle(-20, (float) bubbleHeight, 10);
        buffer.circle(-7, (float) bubbleHeight + 7, 6);
        buffer.circle(0, (float) bubbleHeight + 10, 4);
      } else {
        buffer.circle(20, (float) bubbleHeight, 10);
        buffer.circle(7, (float) bubbleHeight + 7, 6);
        buffer.circle(0, (float) bubbleHeight + 10, 4);
      }
      buffer.pop();
    }

    // draw text
    buffer.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    drawAlignedText(buffer, bubbleWidth);
  }

  private void drawBox(PGraphics buffer) {
    // The width is where the words wrap, not how wide the box comes out: a box
    // is drawn around the longest line it ended up with, so a short text in a
    // wide text does not sit in a mostly empty frame. The stage's own display
    // line is the exception - it is a band along an edge and has to reach from
    // one side to the other whatever it says.
    var wrapWidth = this.width > 0 ? this.width : buffer.width - 16;
    var lines = this.wrap(this.originalText, wrapWidth - 16, buffer);

    var boxWidth = this.displayBand ? wrapWidth : longestLineWidth(lines, buffer) + 16;
    var boxHeight = (this.textSize + 4) * lines.length + 16;
    this.height = boxHeight;
    this.text = String.join("\n", lines);

    buffer.rectMode(PConstants.CORNER);
    buffer.translate((float) (this.x + alignOffset(boxWidth)), (float) (-this.y - boxHeight));
    buffer.stroke(
        (float) this.strokeColor.getRed(),
        (float) this.strokeColor.getGreen(),
        (float) this.strokeColor.getBlue());
    buffer.fill(
        (float) this.backgroundColor.getRed(),
        (float) this.backgroundColor.getGreen(),
        (float) this.backgroundColor.getBlue());
    buffer.strokeWeight(2);
    // A box is rounded all round. The stage's display band is the exception:
    // its bottom two corners sit on the edge of the stage, where a curve would
    // only show the stage behind it.
    var bottomRadius = this.displayBand ? 0 : 16;
    buffer.rect(0, 0, (float) boxWidth, (float) boxHeight, 16, 16, bottomRadius, bottomRadius);

    buffer.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    drawAlignedText(buffer, boxWidth);
  }

  private void drawPlain(PGraphics buffer) {
    var applet = Applet.getInstance();
    var lines = this.originalText.split("\n");
    if (this.width > 0) {
      lines = this.wrap(this.originalText, this.width, buffer);
    }
    if (applet.isDebug()) {
      buffer.push();
      buffer.textSize(12);
      buffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
      buffer.textAlign(PConstants.CENTER);
      buffer.text("(" + this.x + ", " + this.y + ")", 0, -20);
      buffer.pop();
    }
    this.text = String.join("\n", lines);

    buffer.translate((float) this.x, (float) -this.y);
    buffer.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    buffer.textLeading(this.textSize + 4);
    buffer.text(this.text, 8, 8);
  }

  /** @ignore-in-docs Called by the render loop. It needs a Processing buffer, which nothing in
   * the library hands out. */
  public void draw(PGraphics buffer) {
    if (this.stage == null) return;
    if (!this.show || this.originalText == null) return;

    buffer.push();
    // The vertical alignment has to be given every time. Setting only the
    // horizontal one puts the vertical back to the baseline, which drew an
    // aligned text a line higher than an unaligned one.
    buffer.textAlign(
        this.textAlign == TextAlign.DEFAULT ? PApplet.LEFT : this.textAlign.getMode(),
        PApplet.TOP);
    var currentFont = this.fonts.get(this.currentFont);
    buffer.textFont(currentFont.getFont(this.textSize));

    switch (this.style) {
      case SPEAK:
      case THINK:
        {
          this.drawBubble(buffer);
          break;
        }
      case PLAIN:
        {
          this.drawPlain(buffer);
          break;
        }
      case BOX:
        {
          this.drawBox(buffer);
          break;
        }
    }
    buffer.pop();

    if (this.hasLifetime && this.lifetime < System.currentTimeMillis()) {
      this.show = false;
    }
  }
}
