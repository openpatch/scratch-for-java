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

  private Color color = new Color(0);
  private double transparency = 255;
  private double size = 1;
  private List<CopyOnWriteArrayList<Point>> pointsBuffer = new ArrayList<>();
  private boolean down = false;
  private Point previousPoint = null;
  private Stage stage;
  private Sprite sprite;
  private boolean isForeground = false;

  public Pen() {}

  public Pen(Sprite s) {
    this.sprite = s;
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
    this.pointsBuffer = new ArrayList<>();
    this.pointsBuffer.add(new CopyOnWriteArrayList<>());
    this.down = p.down;
    this.sprite = p.sprite;
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
    if (this.down) {
      if (this.pointsBuffer.isEmpty()) {
        this.pointsBuffer.add(new CopyOnWriteArrayList<>());
      }
      this.pointsBuffer
          .get(this.pointsBuffer.size() - 1)
          .add(new Point(x, y, this.color, this.transparency, this.size));
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
    if (!this.down) {
      this.pointsBuffer.add(new CopyOnWriteArrayList<>());
    }
    this.down = true;
  }

  /** Move the pen up. */
  public void up() {
    this.down = false;
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
    this.pointsBuffer.clear();
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
    int pointsBufferSize = this.pointsBuffer.size();
    if (pointsBufferSize <= 0) return;

    Iterator<CopyOnWriteArrayList<Point>> pointsBufferIter = this.pointsBuffer.iterator();

    buffer.push();

    while (pointsBufferIter.hasNext()) {
      CopyOnWriteArrayList<Point> points = pointsBufferIter.next();
      Iterator<Point> pointsIter = points.iterator();

      while (pointsIter.hasNext()) {
        Point point = pointsIter.next();
        if (this.previousPoint != null) {
          buffer.stroke(
              (float) point.color.getRed(),
              (float) point.color.getGreen(),
              (float) point.color.getBlue(),
              (float) point.opacity);
          buffer.strokeWeight((float) point.size);
          buffer.line(
              (float) this.previousPoint.x,
              (float) -this.previousPoint.y,
              (float) point.x,
              (float) -point.y);
        } else if (this.previousPoint == null && !this.down) {
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
          buffer.strokeWeight((float) point.size);
          buffer.circle((float) point.x, (float) -point.y, (float) point.size);
        }
        this.previousPoint = point;
      }
      if (!this.down || pointsBufferIter.hasNext()) {
        this.previousPoint = null;
        pointsBufferIter.remove();
      } else {
        points.clear();
      }
    }
    buffer.pop();
  }
}
