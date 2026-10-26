package org.openpatch.scratch;

import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * A text on the stage: plain, in a box, or in a speech or thought bubble. Every sprite has one
 * for {@code say} and {@code think}; texts of your own are added to a stage with
 * {@code stage.add(text)}.
 *
 * <pre>{@code
 * Text score = new Text("Score: 0", -200, 160, 0);
 * score.setLayer(Layer.UI);
 * stage.add(score);
 * }</pre>
 *
 * <p>Texts are laid out from the font file at their nominal size, in stage units, so where lines
 * break and how large boxes and bubbles are never depends on the window. They are drawn at the
 * window's resolution, in front of the foreground layer (or in the UI layer), and stay sharp.
 */
public class Text {

  /** A sprite's bubble wraps at this width. */
  private static final double BUBBLE_MAX_WIDTH = 330;
  /** A bubble is at least this wide, so that a short word still looks like a bubble. */
  private static final double BUBBLE_MIN_WIDTH = 80;
  private static final double PADDING = 8;

  private static volatile String defaultFont;
  private static volatile int defaultFontGrid;

  private Stage stage;
  private final Sprite sprite;
  private String text = "";
  private double x, y;
  private double width;
  private TextStyle style = TextStyle.PLAIN;
  private TextAlign align = TextAlign.CENTER; // every text is centred on its position, as in 6.x
  private Color textColor = new Color(0, 0, 0);
  private Color backgroundColor = new Color(255, 255, 255);
  private Color strokeColor = new Color(201, 201, 201);
  private String font;
  private final java.util.LinkedHashMap<String, String> fonts = new java.util.LinkedHashMap<>();
  private double size;
  private Layer layer = Layer.MAIN;
  private boolean shown = true;
  private int ticksLeft = -1;
  /** For the stage's display band and the ask box: the box spans this width, whatever it says. */
  private double bandWidth;

  /** An empty plain text at (0, 0). */
  public Text() {
    this.sprite = null;
  }

  /** A sprite's text for say and think. */
  Text(Sprite sprite) {
    this.sprite = sprite;
    this.shown = false;
  }

  /**
   * A plain text.
   *
   * @param text what it says
   * @param x x of its position
   * @param y y of its position (the first line's baseline)
   * @param width where lines wrap; 0 for no wrapping
   */
  public Text(String text, double x, double y, double width) {
    this(text, x, y, width, TextStyle.PLAIN);
  }

  /**
   * A text in a style.
   *
   * @param text what it says
   * @param x x of its position
   * @param y y of its position: the first baseline for plain text, the bottom edge of a box
   * @param width where lines wrap; 0 for no wrapping
   * @param style plain, box, speech or thought bubble
   */
  public Text(String text, double x, double y, double width, TextStyle style) {
    this.sprite = null;
    this.text = text;
    this.x = x;
    this.y = y;
    this.width = width;
    this.style = style;
  }

  // ---- the default font -------------------------------------------------------------

  /**
   * The font every text uses unless it sets its own, from now on. Without it, texts use Ubuntu
   * Mono, or Jersey 10 in pixel-art mode.
   *
   * @param path a TTF or OTF file
   *
   * @example.files TextUseFont.java
   */
  public static void useFont(String path) {
    defaultFont = path;
    defaultFontGrid = 0;
  }

  /**
   * A pixel font as default font, with the size at which one of its pixels is one stage pixel:
   * for a pixel font whose outlines do not show its grid (most do: then {@link #useFont(String)}
   * is enough). In pixel-art mode sizes are rounded to whole pixels of it.
   *
   * @param path a TTF or OTF file
   * @param pixelSize the size at which one font pixel is one stage pixel
   *
   * @example.files TextUseFont.java
   */
  public static void useFont(String path, double pixelSize) {
    Font font = Font.load(path);
    defaultFont = path;
    defaultFontGrid = (int) Math.max(1, Math.round(font.getUnitsPerEm() / pixelSize));
  }

  /**
   * @return the default font file
   *
   * @example.files TextGetDefaultFont.java
   */
  public static String getDefaultFont() {
    String path = defaultFont;
    return path != null ? path : pixelArt() ? Font.PIXEL_ART : Font.DEFAULT;
  }

  /**
   * @return the default font size
   *
   * @example.files TextGetDefaultFontSize.java
   */
  public static double getDefaultFontSize() {
    return pixelArt() ? Font.pixelArtFont().roundToPixelGrid(18) : 14;
  }

  // ---- stage --------------------------------------------------------------------------

  /**
   * Called when the text was added to a stage.
   *
   * @example.files TextWhenAddedToStage.java
   */
  public void whenAddedToStage() {}

  /**
   * Called when the text was removed from its stage.
   *
   * @example.files TextWhenRemovedFromStage.java
   */
  public void whenRemovedFromStage() {}

  /**
   * @return the stage the text is on (a sprite's text: the sprite's stage), or null
   *
   * @example.files TextGetStage.java
   */
  public Stage getStage() {
    return this.sprite != null ? this.sprite.getStage() : this.stage;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Removes the text from its stage.
   *
   * @example.files TextRemove.java
   */
  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  // ---- what it says ---------------------------------------------------------------------

  /**
   * Shows the text with new words until it is changed or hidden.
   *
   * @param text what it says; an empty text hides a bubble
   *
   * @example.files TextShowText.java
   */
  public void showText(String text) {
    this.text = text == null ? "" : text;
    this.shown = true;
    this.ticksLeft = -1;
  }

  /**
   * Shows the text for a number of seconds of game time, then hides it.
   *
   * @param text what it says
   * @param seconds how long
   *
   * @example.files TextShowText.java
   */
  public void showText(String text, double seconds) {
    this.showText(text);
    this.ticksLeft = Math.max(1, (int) Math.round(seconds * 60));
  }

  /** @return what the text says */
  public String getText() {
    return this.text;
  }

  /** Shows the text. */
  public void show() {
    this.shown = true;
  }

  /** Hides the text. */
  public void hide() {
    this.shown = false;
  }

  /** @return whether the text is shown */
  public boolean isVisible() {
    return this.shown && !this.text.isEmpty();
  }

  void setBandWidth(double width) {
    this.bandWidth = width;
  }

  /** One tick of the lifetime of a timed text. */
  void tick() {
    if (this.ticksLeft > 0 && --this.ticksLeft == 0) {
      this.shown = false;
      this.ticksLeft = -1;
    }
  }

  // ---- place and look ----------------------------------------------------------------

  /**
   * @param x x of the position
   * @param y y of the position
   *
   * @example.files TextSetPosition.java
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @param position the position
   *
   * @example.files TextSetPosition.java
   */
  public void setPosition(Vector2 position) {
    this.setPosition(position.getX(), position.getY());
  }

  /**
   * @return the position
   *
   * @example.files TextGetPosition.java
   */
  public Vector2 getPosition() {
    return new Vector2(this.x, this.y);
  }

  /**
   * @param x x of the position
   *
   * @example.files TextSetX.java
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * @return x of the position
   *
   * @example.files TextGetX.java
   */
  public double getX() {
    return this.x;
  }

  /**
   * @param y y of the position
   *
   * @example.files TextSetY.java
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * @return y of the position
   *
   * @example.files TextGetY.java
   */
  public double getY() {
    return this.y;
  }

  /**
   * @param width where lines wrap, in stage units; 0 for no wrapping
   *
   * @example.files TextSetWidth.java
   */
  public void setWidth(double width) {
    this.width = Math.max(0, width);
  }

  /**
   * @return where lines wrap; 0 for no wrapping
   *
   * @example.files TextGetWidth.java
   */
  public double getWidth() {
    return this.width;
  }

  /**
   * @param style plain, box, speech or thought bubble
   *
   * @example.files TextSetStyle.java
   */
  public void setStyle(TextStyle style) {
    this.style = style;
  }

  /**
   * @return the style
   *
   * @example.files TextGetStyle.java
   */
  public TextStyle getStyle() {
    return this.style;
  }

  /**
   * @param align how lines sit relative to the position
   *
   * @example.files TextSetAlign.java
   */
  public void setAlign(TextAlign align) {
    this.align = align == null ? TextAlign.CENTER : align;
  }

  /**
   * @return the alignment
   *
   * @example.files TextGetAlign.java
   */
  public TextAlign getAlign() {
    return this.align;
  }

  /**
   * @param color the colour of the letters
   *
   * @example.files TextSetTextColor.java
   */
  public void setTextColor(Color color) {
    this.textColor = color;
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files TextSetTextColor.java
   */
  public void setTextColor(double red, double green, double blue) {
    this.textColor = new Color(red, green, blue);
  }

  /**
   * @param color the fill of a box or bubble
   *
   * @example.files TextSetBackgroundColor.java
   */
  public void setBackgroundColor(Color color) {
    this.backgroundColor = color;
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files TextSetBackgroundColor.java
   */
  public void setBackgroundColor(double red, double green, double blue) {
    this.backgroundColor = new Color(red, green, blue);
  }

  /**
   * @param color the outline of a box or bubble
   *
   * @example.files TextSetStrokeColor.java
   */
  public void setStrokeColor(Color color) {
    this.strokeColor = color;
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files TextSetStrokeColor.java
   */
  public void setStrokeColor(double red, double green, double blue) {
    this.strokeColor = new Color(red, green, blue);
  }

  /**
   * Adds a font this text can switch to; the first one added is used.
   *
   * @param name the font's name
   * @param path the TTF or OTF file
   *
   * @example.files TextAddFont.java
   */
  public void addFont(String name, String path) {
    if (this.fonts.isEmpty()) {
      this.font = path;
    }
    this.fonts.putIfAbsent(name, path);
  }

  /**
   * @param name the name of a font added with {@link #addFont(String, String)}
   *
   * @example.files TextSwitchFont.java
   */
  public void switchFont(String name) {
    String path = this.fonts.get(name);
    if (path != null) {
      this.font = path;
    }
  }

  /**
   * Switches to the next font added, after the last to the first.
   *
   * @example.files TextNextFont.java
   */
  public void nextFont() {
    if (this.fonts.isEmpty()) {
      return;
    }
    java.util.List<String> paths = new java.util.ArrayList<>(this.fonts.values());
    this.font = paths.get((paths.indexOf(this.font) + 1) % paths.size());
  }

  /**
   * @return the name of the font in use, or null for the default font
   *
   * @example.files TextGetCurrentFontName.java
   */
  public String getCurrentFontName() {
    for (var e : this.fonts.entrySet()) {
      if (e.getValue().equals(this.font)) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * @return the number of the font in use, counting from 0; -1 for the default font
   *
   * @example.files TextGetCurrentFontIndex.java
   */
  public int getCurrentFontIndex() {
    return new java.util.ArrayList<>(this.fonts.values()).indexOf(this.font);
  }

  /**
   * Draws this text in front of the other texts.
   *
   * @example.files TextGoToFrontLayer.java
   * @scratchblock go to [front v] layer
   */
  public void goToFrontLayer() {
    Stage stage = this.getStage();
    if (stage != null && this.sprite == null) {
      stage.reorder(this, Integer.MAX_VALUE);
    }
  }

  /**
   * Draws this text behind the other texts.
   *
   * @example.files TextGoToBackLayer.java
   * @scratchblock go to [back v] layer
   */
  public void goToBackLayer() {
    Stage stage = this.getStage();
    if (stage != null && this.sprite == null) {
      stage.reorder(this, Integer.MIN_VALUE);
    }
  }

  /**
   * @param number how many texts to move in front of
   *
   * @example.files TextGoLayersForwards.java
   * @scratchblock go [forward v] (number) layers
   */
  public void goLayersForwards(int number) {
    Stage stage = this.getStage();
    if (stage != null && this.sprite == null) {
      stage.reorder(this, number);
    }
  }

  /**
   * @param number how many texts to move behind
   *
   * @example.files TextGoLayersBackwards.java
   * @scratchblock go [backward v] (number) layers
   */
  public void goLayersBackwards(int number) {
    this.goLayersForwards(-number);
  }

  /**
   * @param path the font file for this text, or null for the default font
   *
   * @example.files TextSetFont.java
   */
  public void setFont(String path) {
    this.font = path;
  }

  /**
   * @return the font file, or null for the default font
   *
   * @example.files TextGetFont.java
   */
  public String getFont() {
    return this.font;
  }

  /**
   * @param size the font size in stage units (in pixel-art mode rounded to whole font pixels when
   *     drawn)
   *
   * @example.files TextSetTextSize.java
   */
  public void setTextSize(double size) {
    this.size = Math.max(1, size);
  }

  /**
   * @return the font size, or the default
   *
   * @example.files TextGetTextSize.java
   */
  public double getTextSize() {
    return this.size > 0 ? this.size : this.defaultSize();
  }

  /**
   * Moves the text to another layer: {@link Layer#UI} keeps it in place when the camera moves;
   * any other layer puts it into the world, in front of the foreground.
   *
   * @param layer the layer
   */
  public void setLayer(Layer layer) {
    this.layer = layer;
  }

  /** @return the layer */
  public Layer getLayer() {
    return this.layer;
  }

  // ---- layout ---------------------------------------------------------------------

  private static boolean pixelArt() {
    Game game = Game.currentOrNull();
    return game != null && game.isPixelArt();
  }

  private double defaultSize() {
    return getDefaultFontSize();
  }

  Font resolveFont() {
    if (this.font != null) {
      return Font.load(this.font);
    }
    String path = defaultFont;
    if (path != null) {
      Font f = Font.load(path);
      return defaultFontGrid > 0 ? f.withPixelGrid(defaultFontGrid) : f;
    }
    return pixelArt() ? Font.pixelArtFont() : Font.defaultFont();
  }

  /** The layout as it would be drawn now: font, size and wrapping resolved. */
  TextLayout layout() {
    boolean bubble = this.style == TextStyle.SPEAK || this.style == TextStyle.THINK;
    double wrap = bubble && (this.sprite != null || this.width <= 0) ? BUBBLE_MAX_WIDTH
        : this.bandWidth > 0 ? this.bandWidth : this.width;
    double inner = this.style == TextStyle.PLAIN ? wrap : wrap - 2 * PADDING;
    return this.layoutOf(this.text, wrap > 0 ? Math.max(1, inner) : 0);
  }

  /** Other words in this text's font and size (the ask box draws its own card around them). */
  TextLayout layoutOf(String words, double maxWidth) {
    Font f = this.resolveFont();
    double s = this.getTextSize();
    if (pixelArt()) {
      s = f.roundToPixelGrid(s);
    }
    return TextLayout.of(words, f, s, maxWidth);
  }

  /** The draw-list entry, or null when nothing is shown. */
  TextDraw toDraw() {
    if (!this.isVisible()) {
      return null;
    }
    TextLayout layout = this.layout();
    double anchorX = this.x, anchorY = this.y;
    if (this.sprite != null) {
      // a sprite's bubble hangs off the top right corner of the painted pixels
      Shape outline = this.sprite.paintedOutline();
      if (outline == null) {
        anchorX = this.sprite.getX();
        anchorY = this.sprite.getY();
      } else {
        Bounds b = outline.getBounds();
        anchorX = b.x() + b.width();
        anchorY = b.y() + b.height();
      }
    }
    int alignCode = switch (this.align) {
      case LEFT -> 0;
      case RIGHT -> 2;
      default -> 1; // centred on its position, as 6.x had every text
    };
    TextDraw.Box box = switch (this.style) {
      case PLAIN -> TextDraw.Box.NONE;
      case BOX -> TextDraw.Box.PLAIN;
      case SPEAK -> TextDraw.Box.SPEECH;
      case THINK -> TextDraw.Box.THOUGHT;
    };
    double boxWidth, boxHeight, padding;
    if (box == TextDraw.Box.NONE) {
      padding = 0;
      boxWidth = layout.width();
      boxHeight = layout.height();
    } else {
      padding = PADDING;
      boxWidth = layout.width() + 2 * PADDING;
      if (box != TextDraw.Box.PLAIN) {
        boxWidth = Math.max(BUBBLE_MIN_WIDTH, boxWidth);
      }
      boxHeight = layout.height() + 2 * PADDING;
      if (this.bandWidth > 0) {
        boxWidth = this.bandWidth;
      }
    }
    // horizontally: the anchor is the left edge, the centre or the right edge of the box
    double left = switch (alignCode) {
      case 1 -> anchorX - boxWidth / 2;
      case 2 -> anchorX - boxWidth;
      default -> anchorX;
    };
    // vertically: plain text's first line is centred on its position and further lines hang
    // below (6.x, Processing's textAlign CENTER: a number put under a line sits on it); boxes
    // stand on their bottom edge
    double top = box == TextDraw.Box.NONE
        ? anchorY + (layout.font().ascent(layout.size()) + layout.font().descent(layout.size())) / 2
        : anchorY + boxHeight;
    double tailX = anchorX;
    Stage stage = this.getStage();
    if (this.sprite != null) {
      // a bubble grows to the right of the sprite, or to its left when there is no room, and
      // stays below the stage's top edge, as in Scratch
      Shape outline = this.sprite.paintedOutline();
      double camX = 0, camY = 0, zoom = 1;
      if (stage != null && this.sprite.getLayer() != Layer.UI) {
        camX = stage.getCamera().getX();
        camY = stage.getCamera().getY();
        zoom = stage.getCamera().getZoom() / 100;
      }
      double halfW = stage == null ? Double.MAX_VALUE : stage.getWidth() / 2.0 / zoom;
      double halfH = stage == null ? Double.MAX_VALUE : stage.getHeight() / 2.0 / zoom;
      left = anchorX;
      if (left + boxWidth > camX + halfW && outline != null) {
        double leftEdge = outline.getBounds().x();
        left = leftEdge - boxWidth;
        tailX = leftEdge; // the tail starts at the sprite's left edge: drawn mirrored
      }
      // neither side has room: keep it on the stage anyway
      left = Math.max(camX - halfW, Math.min(left, camX + halfW - boxWidth));
      top = Math.min(top, camY + halfH);
    }
    return new TextDraw(layout, left, top, boxWidth, boxHeight, alignCode, this.textColor.toARGB(), box,
        this.backgroundColor.toARGB(), this.strokeColor.toARGB(), padding, tailX);
  }
}
