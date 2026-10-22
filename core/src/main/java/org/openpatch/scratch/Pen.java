package org.openpatch.scratch;

/**
 * A pen draws lines onto the canvas of its layer, like Scratch's pen. Every sprite has one
 * ({@code getPen()}) that follows it; a pen of your own can be added to a stage and moved with
 * {@link #setPosition(double, double)}.
 *
 * <p>What a pen draws stays where it was drawn on the screen: a camera moving later does not move
 * it. A sprite's pen draws onto {@link Layer#BACKGROUND}, behind the sprites, unless it is moved
 * with {@link #setLayer(Layer)}.
 *
 * <pre>{@code
 * this.getPen().setColor(new Color(255, 0, 0));
 * this.getPen().setSize(4);
 * this.getPen().down();
 * this.move(50);
 * }</pre>
 */
public class Pen {

  private Stage stage;
  private Sprite sprite;
  private Layer layer = Layer.BACKGROUND;
  private Color color = new Color(0, 0, 255);
  private double size = 1;
  private double transparency;
  private boolean down;
  private double x, y;

  /** A pen of your own, at (0, 0); add it to a stage with {@code stage.add(pen)}. */
  public Pen() {}

  /** The pen of a sprite: it follows the sprite. */
  Pen(Sprite sprite) {
    this.sprite = sprite;
  }

  /** A copy of {@code pen} for a clone: settings and up/down, as Scratch copies the pen state. */
  Pen(Pen pen, Sprite sprite) {
    this.sprite = sprite;
    this.layer = pen.layer;
    this.color = pen.color;
    this.size = pen.size;
    this.transparency = pen.transparency;
    this.down = pen.down;
    this.x = pen.x;
    this.y = pen.y;
  }

  private Stage stage() {
    return this.sprite != null ? this.sprite.getStage() : this.stage;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * @return the colour lines are drawn in
   *
   * @example.files PenGetColor.java
   */
  public Color getColor() {
    return this.color;
  }

  /**
   * @param color the colour to draw lines in
   *
   * @example.files PenSetColor.java
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * Sets the colour by Scratch's colour number (hue) from 0 to 100, at full saturation and
   * brightness, like Scratch's "set pen color to".
   *
   * @param hue the colour from 0 to 100
   *
   * @example.files PenSetColor.java
   */
  public void setColor(double hue) {
    this.color = Color.fromHSB(hue, 100, 100).withAlpha(this.color.getAlpha());
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files PenSetColor.java
   */
  public void setColor(double red, double green, double blue) {
    this.color = new Color(red, green, blue);
  }

  /**
   * @param by how much to change the colour (hue, 0 to 100, going around)
   *
   * @example.files PenChangeColor.java
   * @scratchblock change pen [color v] by (c)
   */
  public void changeColor(double by) {
    this.color = this.color.withHue(this.color.getHue() + by);
  }

  /**
   * @return the line width in stage pixels
   *
   * @example.files PenGetSize.java
   */
  public double getSize() {
    return this.size;
  }

  /**
   * @param size the line width in stage pixels (at least 1)
   *
   * @example.files PenSetSize.java
   * @scratchblock set pen size to (size)
   */
  public void setSize(double size) {
    this.size = Math.max(1, size);
  }

  /**
   * @param by how much to change the line width
   *
   * @example.files PenChangeSize.java
   * @scratchblock change pen size by (size)
   */
  public void changeSize(double by) {
    this.setSize(this.size + by);
  }

  /** @return the transparency in percent (0 opaque, 100 invisible) */
  public double getTransparency() {
    return this.transparency;
  }

  /**
   * @param transparency in percent (0 opaque, 100 invisible)
   *
   * @example.files PenSetTransparency.java
   */
  public void setTransparency(double transparency) {
    this.transparency = Math.max(0, Math.min(100, transparency));
  }

  /**
   * @param by how much to change the transparency
   *
   * @example.files PenChangeTransparency.java
   */
  public void changeTransparency(double by) {
    this.setTransparency(this.transparency + by);
  }

  /** @return the layer whose canvas the pen draws on */
  public Layer getLayer() {
    return this.layer;
  }

  /** @param layer the layer whose canvas the pen draws on */
  public void setLayer(Layer layer) {
    this.layer = layer;
  }

  /** @return whether the pen is down and draws when it moves */
  public boolean isDown() {
    return this.down;
  }

  /**
   * Puts the pen down: it leaves a dot, and a line wherever it moves.
   *
   * @example.files PenDown.java
   * @scratchblock pen down
   */
  public void down() {
    this.down = true;
    this.draw(this.x, this.y, this.x, this.y);
  }

  /**
   * Lifts the pen: it no longer draws.
   *
   * @example.files PenUp.java
   * @scratchblock pen up
   */
  public void up() {
    this.down = false;
  }

  /**
   * Moves the pen, drawing a line from where it was if it is down.
   *
   * @param x world x
   * @param y world y
   *
   * @example.files PenSetPosition.java
   */
  public void setPosition(double x, double y) {
    double fromX = this.x, fromY = this.y;
    this.x = x;
    this.y = y;
    if (this.down) {
      this.draw(fromX, fromY, x, y);
    }
  }

  /**
   * @param position world position
   *
   * @example.files PenSetPosition.java
   */
  public void setPosition(Vector2 position) {
    this.setPosition(position.getX(), position.getY());
  }

  /**
   * Moves the pen to a random position on the stage, drawing if it is down.
   *
   * @example.files PenGoToRandomPosition.java
   */
  public void goToRandomPosition() {
    Stage stage = this.stage();
    double halfW = stage == null ? 240 : stage.getWidth() / 2.0, halfH = stage == null ? 180 : stage.getHeight() / 2.0;
    this.setPosition(Random.random(-halfW, halfW), Random.random(-halfH, halfH));
  }

  /**
   * Moves the pen to the mouse pointer, drawing if it is down.
   *
   * @example.files PenGoToMousePointer.java
   */
  public void goToMousePointer() {
    Stage stage = this.stage();
    if (stage != null) {
      this.setPosition(stage.getMouse());
    }
  }

  /** @return the pen's world position */
  public Vector2 getPosition() {
    return new Vector2(this.x, this.y);
  }

  /**
   * Stamps the pen's sprite onto the pen's layer, like Scratch's "stamp" block.
   *
   * @scratchblock stamp
   * @example.files PenStamp.java
   */
  public void stamp() {
    if (this.sprite != null) {
      this.sprite.stamp(this.layer);
    }
  }

  /**
   * Erases everything drawn on the pen's layer, by any pen or stamp.
   *
   * @example.files PenEraseAll.java
   * @scratchblock erase all
   */
  public void eraseAll() {
    Stage stage = this.stage();
    if (stage != null) {
      stage.eraseLayer(this.layer);
    }
  }

  /** Where a sprite's pen is when the sprite moves without drawing (going to a new stage, say). */
  void jumpTo(double x, double y) {
    this.x = x;
    this.y = y;
  }

  private void draw(double fromX, double fromY, double toX, double toY) {
    Stage stage = this.stage();
    if (stage == null) {
      return;
    }
    int argb = this.color.withAlpha(this.color.getAlpha() * (1 - this.transparency / 100)).toARGB();
    stage.penLine(this.layer, fromX, fromY, toX, toY, this.size, argb);
  }
}
