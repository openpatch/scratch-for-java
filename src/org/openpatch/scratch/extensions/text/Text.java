package org.openpatch.scratch.extensions.text;

import java.util.concurrent.CopyOnWriteArrayList;
import org.davidmoten.text.utils.WordWrap;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Color;
import org.openpatch.scratch.internal.Font;
import processing.core.PApplet;
import processing.core.PConstants;

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
  private TextStyle style;
  private Color textColor;
  private Color backgroundColor;
  private Color strokeColor;
  private int textAlign = -1;

  private static int SPEAK_BUBBLE_MAX_LIMIT = 330;
  private static int SPEAK_BUBBLE_MIN_LIMIT = 80;
  private static int DEFAULT_TEXT_SIZE = 14;

  /**
   * Constructs a new Text object with default values. The text is initialized to an empty string,
   * positioned at (0, 0), with a default size of 0. The text style is set to plain and the text
   * alignment is set to center.
   */
  public Text() {
    this("", 0, 0, 0);
    this.style = TextStyle.PLAIN;
    this.textAlign = TextAlign.CENTER;
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
    this.textSize = DEFAULT_TEXT_SIZE;
    this.originalText = text;
    this.width = width;
    this.show = false;
    this.backgroundColor = new Color(255, 255, 255);
    this.textColor = new Color(120, 120, 120);
    this.strokeColor = new Color(218, 218, 218);
    this.fontName = Font.defaultFontName;
    this.addFont(Font.defaultFontName, Font.defaultFontPath);
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

  public void addedToStage(Stage stage) {
    this.stage = stage;
    this.whenAddedToStage();
    this.whenAddedToStage(stage);
  }

  public void removedFromStage(Stage stage) {
    this.stage = null;
    this.whenRemovedFromStage();
    this.whenRemovedFromStage(stage);
  }

  /**
   * This method is called when the object is added to the stage. Override this method to define
   * custom behavior when the object is added to the stage.
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
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * Adds a new font to the list of fonts if it does not already exist.
   *
   * @param name the name of the font to be added
   * @param path the path to the font file
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
   */
  public void nextFont() {
    this.currentFont = (this.currentFont + 1) % this.fonts.size();
  }

  /**
   * Retrieves the name of the current font.
   *
   * @return the name of the current font, or {@code null} if no fonts are available.
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
   */
  public int getCurrentFontIndex() {
    return this.currentFont;
  }

  /**
   * Sets the position of the text
   *
   * @param x a x coordinate
   * @param y a y coordinate
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
   */
  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  /**
   * Sets the x-coordinate for this object.
   *
   * @param x the new x-coordinate value
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Returns the x-coordinate of this object.
   *
   * @return the x-coordinate as a double
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the Y coordinate of the text.
   *
   * @param y the new Y coordinate value
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Returns the y-coordinate of this object.
   *
   * @return the y-coordinate as a double
   */
  public double getY() {
    return this.y;
  }

  /**
   * Displays the specified text.
   *
   * @param text The text to be displayed. If the text is null or empty, the text will be set to
   *     null.
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
   */
  public void setStyle(TextStyle style) {
    this.style = style;
  }

  /**
   * Sets the background color using the specified RGB values.
   *
   * @param r the red component of the color (0-255)
   * @param g the green component of the color (0-255)
   * @param b the blue component of the color (0-255)
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
   */
  public void setFont(String name) {
    this.fontName = name;
  }

  /**
   * Retrieves the name of the font.
   *
   * @return the name of the font as a String.
   */
  public String getFont() {
    return this.fontName;
  }

  /**
   * Sets the size of the text.
   *
   * @param size the new size of the text
   */
  public void setTextSize(int size) {
    this.textSize = size;
  }

  /**
   * Returns the size of the text.
   *
   * @return the size of the text as an integer
   */
  public int getTextSize() {
    return this.textSize;
  }

  /**
   * Returns the width of the text.
   *
   * @return the width of the text as a double
   */
  public double getWidth() {
    return this.width;
  }

  /**
   * Sets the width of the text.
   *
   * @param width the new width to set
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Sets the alignment of the text.
   *
   * @param align the alignment value to set. This could be a constant representing left, center, or
   *     right alignment.
   */
  public void setAlign(int align) {
    this.textAlign = align;
  }

  /**
   * Sets the UI status of this object.
   *
   * @param isUI a boolean indicating whether this object is part of the UI
   */
  public void setIsUI(boolean isUI) {
    this.isUI = isUI;
  }

  /**
   * Checks if the current instance is a UI element.
   *
   * @return {@code true} if this instance is a UI element, {@code false} otherwise.
   */
  public boolean isUI() {
    return this.isUI;
  }

  private String[] wrap(String text, double maxWidth) {
    var applet = Applet.getInstance();
    return WordWrap.from(text)
        .maxWidth(maxWidth)
        .breakWords(true)
        .stringWidth(s -> applet.textWidth(s.toString()))
        .wrap()
        .split("\n");
  }

  private void drawBubble() {
    var applet = Applet.getInstance();
    var lines = this.wrap(this.originalText, SPEAK_BUBBLE_MAX_LIMIT);
    var maxLineWidth = 0.0f;
    for (var line : lines) {
      var lineWidth = applet.textWidth(line);
      if (lineWidth > maxLineWidth) {
        maxLineWidth = lineWidth;
      }
    }
    maxLineWidth = Math.max(maxLineWidth, SPEAK_BUBBLE_MIN_LIMIT);
    if (this.sprite == null) {
      return;
    }

    this.y = this.sprite.getY() + this.sprite.getHeight() * 1.1 / 2.0;
    this.x = this.sprite.getX() + this.sprite.getWidth() * 0.9 / 2.0;

    this.width = maxLineWidth + 16;
    this.text = String.join("\n", lines);
    this.height = (this.textSize + 4) * lines.length + 16;
    applet.rectMode(PApplet.CORNER);
    var mirror = false;
    applet.translate((float) x, (float) (-y - this.height));
    applet.stroke(
        (float) this.strokeColor.getRed(),
        (float) this.strokeColor.getGreen(),
        (float) this.strokeColor.getBlue());
    applet.fill(
        (float) this.backgroundColor.getRed(),
        (float) this.backgroundColor.getGreen(),
        (float) this.backgroundColor.getBlue());
    applet.rect(0, 0, (float) this.width, (float) this.height, 16, 16, 16, 16);
    if (this.style == TextStyle.SPEAK) {
      applet.push();
      applet.fill(
          (float) this.backgroundColor.getRed(),
          (float) this.backgroundColor.getGreen(),
          (float) this.backgroundColor.getBlue());
      if (mirror) {
        applet.translate((float) this.width - 40, (float) this.height);
        applet.triangle(20, 0, 0, 0, 20, 20);
      } else {
        applet.translate(10, (float) this.height);
        applet.triangle(0, 20, 0, 0, 20, 0);
      }
      applet.stroke(
          (float) this.backgroundColor.getRed(),
          (float) this.backgroundColor.getGreen(),
          (float) this.backgroundColor.getBlue());
      applet.strokeWeight(3);
      applet.line(2, 0, 16, 0);
      applet.pop();
    } else if (this.style == TextStyle.THINK) {
      applet.push();
      if (mirror) {
        applet.translate((float) this.width - 10, 0);
        applet.circle(-20, (float) this.height, 10);
        applet.circle(-7, (float) this.height + 7, 6);
        applet.circle(0, (float) this.height + 10, 4);
      } else {
        applet.circle(20, (float) this.height, 10);
        applet.circle(7, (float) this.height + 7, 6);
        applet.circle(0, (float) this.height + 10, 4);
      }
      applet.pop();
    }

    // draw text
    applet.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    applet.textLeading(this.textSize + 4);
    applet.text(this.text, 8, 8);
  }

  private void drawBox() {
    var applet = Applet.getInstance();
    var lines = this.originalText.split("\n");

    if (this.width == 0) {
      this.width = applet.getWidth() - 16; // padding
    }
    if (this.width > 0) {
      lines = this.wrap(this.originalText, this.width - 16); // padding
    }
    this.height = (this.textSize + 4) * lines.length + 16;
    this.text = String.join("\n", lines);

    applet.rectMode(PApplet.CORNER);
    applet.translate((float) this.x, (float) (-this.y - this.height));
    applet.stroke(
        (float) this.strokeColor.getRed(),
        (float) this.strokeColor.getGreen(),
        (float) this.strokeColor.getBlue());
    applet.fill(
        (float) this.backgroundColor.getRed(),
        (float) this.backgroundColor.getGreen(),
        (float) this.backgroundColor.getBlue());
    applet.strokeWeight(2);
    applet.rect(0, 0, (float) this.width, (float) this.height, 16, 16, 0, 0);

    applet.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    applet.textLeading(this.textSize + 4);
    applet.text(this.text, 8, 8);
  }

  private void drawPlain() {
    var applet = Applet.getInstance();
    var lines = this.originalText.split("\n");
    if (this.width > 0) {
      lines = this.wrap(this.originalText, this.width);
    }
    if (applet.isDebug()) {
      var textSize = applet.getTextSize();
      applet.textSize(12);
      applet.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
      applet.textAlign(PConstants.CENTER);
      applet.text("(" + this.x + ", " + this.y + ")", 0, -20);
      applet.textSize((float) textSize);
    }
    this.text = String.join("\n", lines);

    applet.translate((float) this.x, (float) -this.y);
    applet.fill(
        (float) this.textColor.getRed(),
        (float) this.textColor.getGreen(),
        (float) this.textColor.getBlue());
    applet.textLeading(this.textSize + 4);
    applet.text(this.text, 8, 8);
  }

  public void draw() {
    if (this.stage == null) return;
    if (!this.show || this.originalText == null) return;

    var applet = Applet.getInstance();

    applet.push();
    if (this.textAlign != TextAlign.DEFAULT) {
      applet.textAlign(this.textAlign);
    } else {
      applet.textAlign(PApplet.LEFT, PApplet.TOP);
    }
    applet.textFont(this.fonts.get(this.currentFont).getFont(this.textSize));
    applet.textSize(this.textSize);

    switch (this.style) {
      case SPEAK:
      case THINK:
        {
          this.drawBubble();
          break;
        }
      case PLAIN:
        {
          this.drawPlain();
          break;
        }
      case BOX:
        {
          this.drawBox();
          break;
        }
    }
    applet.pop();

    if (this.hasLifetime && this.lifetime < System.currentTimeMillis()) {
      this.show = false;
    }
  }
}
