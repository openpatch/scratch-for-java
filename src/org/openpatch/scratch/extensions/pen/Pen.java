package org.openpatch.scratch.extensions.pen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.color.Color;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Vector2;
import processing.core.PGraphics;

/**
 * The Pen class represents a drawing tool that can be used to draw paths on a stage. It supports
 * various functionalities such as setting color, size, transparency, moving to different positions,
 * and stamping the current sprite onto the canvas. The Pen can be associated with a Sprite and can
 * draw on both the foreground and background.
 */
public class Pen {

  class Point {

    double x;
    double y;
    Color color;
    double opacity;
    double size;

    Point(double x, double y, Color color, double opacity, double size) {
      this.x = x;
      this.y = y;
      this.color = new Color(color);
      this.opacity = opacity;
      this.size = size;
    }

    public String toString() {
      return "(" + x + ", " + y + ") at " + size;
    }
  }

  class Path {
    private CopyOnWriteArrayList<Point> points;
    private boolean closed;
    private Point lastPoint;

    Path() {
      this.points = new CopyOnWriteArrayList<>();
      closed = false;
    }

    void add(Point point) {
      this.points.add(point);
    }
  }

  private Color color = new Color(0);
  private double transparency = 255;
  private double size = 1;
  private List<Path> pathsBuffer = new ArrayList<>();
  private boolean down = false;
  private Path currentPath = null;
  private Stage stage;
  private Sprite sprite;
  private boolean isForeground = false;
  private double x;
  private double y;

  /** Constructs a new Pen object. */
  public Pen() {}

  /**
   * Constructs a new Pen object associated with the given Sprite.
   *
   * @param s the Sprite object to associate with this Pen
   */
  public Pen(Sprite s) {
    this.sprite = s;
    this.x = s.getX();
    this.y = s.getY();
  }

  /**
   * Copies a Pen object.
   *
   * @param p Pen object to copy
   */
  public Pen(Pen p) {
    this.color = new Color(p.color);
    this.size = p.size;
    this.transparency = p.transparency;
    this.pathsBuffer = new ArrayList<>();
    this.sprite = p.sprite;
    this.x = p.x;
    this.y = p.y;
    if (p.down) {
      this.down();
    }
  }

  /**
   * This method is called when the pen is added to the stage. It assigns the given stage to the
   * pen's stage variable.
   *
   * @param stage The stage to which the pen is added.
   */
  public void addedToStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * This method is called when the pen is removed from the stage. It sets the stage reference to
   * null.
   *
   * @param stage The stage from which the pen is removed.
   */
  public void removedFromStage(Stage stage) {
    this.stage = null;
  }

  /**
   * Retrieves the current color of the pen in HSB (Hue, Saturation, Brightness) format.
   *
   * @return the color of the pen as a double representing the HSB value.
   */
  public double getColor() {
    return this.color.getHSB();
  }

  /**
   * Set color via hue value
   *
   * @param h a hue value [0...255]
   */
  public void setColor(double h) {
    this.color.setHSB(h);
  }

  /**
   * Set color via rgb values
   *
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setColor(double r, double g, double b) {
    this.color.setRGB(r, g, b);
  }

  /**
   * Change color via a hue value, which is added to the current hue value
   *
   * @param c a hue value [0...255]
   */
  public void changeColor(double c) {
    this.color.changeColor(c);
  }

  /**
   * Sets the pen to the background. This method changes the state of the pen to indicate that it is
   * no longer in the foreground.
   */
  public void goToBackground() {
    this.isForeground = false;
  }

  /**
   * Moves the pen to the foreground. This method sets the pen's state to indicate that it is in the
   * foreground.
   */
  public void goToForeground() {
    this.isForeground = true;
  }

  /**
   * Returns true if the pen is in the background.
   *
   * @return true if the pen is in the background
   */
  public boolean isInBackground() {
    return !this.isForeground;
  }

  /**
   * Set the size of the pen
   *
   * @param size size of the pen
   */
  public void setSize(double size) {
    this.size = size;
  }

  /**
   * Returns the size of the pen
   *
   * @return the size of the pen
   */
  public double getSize() {
    return this.size;
  }

  /**
   * Change the size of the pen
   *
   * @param size size to change
   */
  public void changeSize(double size) {
    this.size += size;
  }

  /**
   * Set the transparency
   *
   * @param transparency transparency of the pen
   */
  public void setTransparency(double transparency) {
    this.transparency = transparency;
  }

  /**
   * Change the transparency by a step
   *
   * @param step the step to change the transparency by
   */
  public void changeTransparency(double step) {
    this.setTransparency((this.transparency + step) % 255);
  }

  /**
   * Set the position if the pen is down.
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
    if (this.down && this.currentPath != null) {
      this.currentPath.add(new Point(x, y, this.color, this.transparency, this.size));
    }
  }

  /**
   * Sets the position of the pen using a Vector2 object.
   *
   * @param v the Vector2 object containing the new x and y coordinates
   */
  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  /**
   * Moves the object to a random position within the boundaries of the stage. The new position is
   * determined by generating random coordinates within the width and height of the stage.
   */
  public void goToRandomPosition() {
    this.setPosition(
        Random.randomInt(-this.stage.getWidth() / 2, this.stage.getWidth() / 2),
        Random.randomInt(-this.stage.getHeight() / 2, this.stage.getHeight() / 2));
  }

  /** Moves the object to the current position of the mouse pointer. */
  public void goToMousePointer() {
    this.setPosition(this.stage.getMouseX(), this.stage.getMouseY());
  }

  /** Set the pen down. */
  public void down() {
    this.down = true;
    if (this.currentPath == null || this.currentPath.closed) {
      this.currentPath = new Path();
      this.currentPath.add(new Point(this.x, this.y, this.color, this.transparency, this.size));
      this.pathsBuffer.add(currentPath);
    }
  }

  /** Move the pen up. */
  public void up() {
    this.down = false;
    if (this.currentPath != null) {
      this.currentPath.closed = true;
      this.currentPath = null;
    }
  }

  /**
   * Stamps the current sprite onto the canvas. If the sprite is not null, it will stamp either to
   * the foreground or the background based on the value of the isForeground flag.
   */
  public void stamp() {
    if (this.sprite != null) {
      if (this.isForeground) {
        this.sprite.stampToForeground();
      } else {
        this.sprite.stampToBackground();
      }
    }
  }

  /** Erases all. */
  public void eraseAll() {
    this.pathsBuffer.clear();
    if (this.stage != null) {
      if (this.isForeground) {
        this.stage.eraseForeground();
      } else {
        this.stage.eraseBackground();
      }
    }
  }

  /** Draw the line which the pen has drawn. */
  public void draw() {
    if (this.stage == null) return;
    PGraphics buffer = this.stage.getBackgroundBuffer();
    if (!this.isInBackground()) {
      buffer = this.stage.getForegroundBuffer();
    }
    int pointsBufferSize = this.pathsBuffer.size();
    if (pointsBufferSize <= 0) return;

    Iterator<Path> pathsBufferIter = this.pathsBuffer.iterator();

    buffer.push();

    while (pathsBufferIter.hasNext()) {
      var path = pathsBufferIter.next();
      var points = path.points;
      var pointsIter = points.iterator();

      while (pointsIter.hasNext()) {
        Point point = pointsIter.next();
        buffer.stroke(
            (float) point.color.getRed(),
            (float) point.color.getGreen(),
            (float) point.color.getBlue(),
            (float) point.opacity);
        buffer.fill(
            (float) point.color.getRed(),
            (float) point.color.getGreen(),
            (float) point.color.getBlue(),
            (float) point.opacity);
        buffer.strokeWeight(0);
        buffer.circle((float) point.x, (float) -point.y, (float) point.size / 2.0f);

        if (path.lastPoint != null) {
          buffer.stroke(
              (float) point.color.getRed(),
              (float) point.color.getGreen(),
              (float) point.color.getBlue(),
              (float) point.opacity);
          buffer.strokeWeight((float) point.size);
          buffer.line(
              (float) path.lastPoint.x,
              (float) -path.lastPoint.y,
              (float) point.x,
              (float) -point.y);
        }
        path.lastPoint = point;
      }
      if (path.closed) {
        pathsBufferIter.remove();
      } else {
        points.clear();
      }
    }
    buffer.pop();
  }
}
