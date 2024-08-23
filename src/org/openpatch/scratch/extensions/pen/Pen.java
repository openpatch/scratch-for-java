package org.openpatch.scratch.extensions.pen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.internal.Color;
import processing.core.PGraphics;

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

  public Pen() {}

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

  public void addedToStage(Stage stage) {
    this.stage = stage;
  }

  public void removedFromStage(Stage stage) {
    this.stage = null;
  }

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

  public void goToBackground() {
    this.isForeground = false;
  }

  public void goToForeground() {
    this.isForeground = true;
  }

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

  /** Changes the size of the pen */
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

  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  public void goToRandomPosition() {
    this.setPosition(
        Random.randomInt(-this.stage.getWidth() / 2, this.stage.getWidth() / 2),
        Random.randomInt(-this.stage.getHeight() / 2, this.stage.getHeight() / 2));
  }

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

  public void stamp() {
    if (this.sprite != null) {
      if (this.isForeground) {
        this.sprite.stampToForeground();
      } else {
        this.sprite.stampToBackground();
      }
    }
  }

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
