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

  public Text() {
    this("", 0, 0, 0);
    this.style = TextStyle.PLAIN;
    this.textAlign = TextAlign.CENTER;
  }

  public Text(Sprite s) {
    this(null, 0, 0, 0);
    this.sprite = s;
    this.style = TextStyle.SPEAK;
  }

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

  public Text(String text, double x, double y, double width, TextStyle style) {
    this(text, x, y, width);
    this.style = style;
  }

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

  public void whenAddedToStage() {}

  public void whenAddedToStage(Stage stage) {}

  public void whenRemovedFromStage() {}

  public void whenRemovedFromStage(Stage stage) {}

  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  public Stage getStage() {
    return this.stage;
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
    for (int i = 0; i < this.fonts.size(); i++) {
      Font font = this.fonts.get(i);
      if (font.getName().equals(name)) {
        this.currentFont = i;
        return;
      }
    }
  }

  public void nextFont() {
    this.currentFont = (this.currentFont + 1) % this.fonts.size();
  }

  public String getCurrentFontName() {
    if (this.fonts.size() == 0) {
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
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getX() {
    return this.x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getY() {
    return this.y;
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
    this.showText(text);
    this.hasLifetime = true;
    this.lifetime = System.currentTimeMillis() + millis;
  }

  public void setStyle(TextStyle style) {
    this.style = style;
  }

  public void setBackgroundColor(int r, int g, int b) {
    this.backgroundColor = new Color(r, g, b);
  }

  public void setBackgroundColor(double h) {
    this.backgroundColor = new Color(h);
  }

  public void setBackgroundColor(Color c) {
    this.backgroundColor = c;
  }

  public void setTextColor(int r, int g, int b) {
    this.textColor = new Color(r, g, b);
  }

  public void setTextColor(double h) {
    this.textColor = new Color(h);
  }

  public void setTextColor(Color c) {
    this.textColor = c;
  }

  public void setStrokeColor(int r, int g, int b) {
    this.strokeColor = new Color(r, g, b);
  }

  public void setStrokeColor(double h) {
    this.strokeColor = new Color(h);
  }

  public void setStrokeColor(Color c) {
    this.strokeColor = c;
  }

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

  public double getWidth() {
    return this.width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public void setAlign(int align) {
    this.textAlign = align;
  }

  public void setIsUI(boolean isUI) {
    this.isUI = isUI;
  }

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

    this.y = this.sprite.getY() + this.sprite.getHeight() * 1.1 / 2;
    this.x = this.sprite.getX() + this.sprite.getWidth() * 0.9 / 2.0;

    this.width = maxLineWidth + 16;
    this.text = String.join("\n", lines);
    this.height = (this.textSize + 4) * lines.length + 16;
    applet.rectMode(PApplet.CORNER);

    // bound for top
    var y = Math.min(applet.getHeight() / 2.0, this.y + this.height);

    // bound for bottom
    if (y - this.height < -applet.getHeight() / 2.0) {
      y = -applet.getHeight() / 2.0 + this.height;
    }

    // bound for right side
    var x = Math.min(applet.getWidth() / 2.0 - this.width, this.x + this.width);

    // bound for left side
    var mirror = false;
    if (x - this.width < -applet.getWidth() / 2.0) {
      mirror = true;
    }

    if (mirror) {
      x = (this.sprite.getX() - this.sprite.getWidth() * 0.9 / 2 - this.width);
      if (x < 0) {
        x = 0;
      }
      applet.translate((float) x, (float) -y);
    } else {
      applet.translate((float) x, (float) -y);
    }
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
      applet.textSize(12);
      applet.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
      applet.textAlign(PConstants.CENTER);
      applet.text("(" + this.x + ", " + this.y + ")", 0, -20);
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
