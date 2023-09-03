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
import org.openpatch.scratch.internal.Drawable;
import processing.core.PGraphics;

public class Pen implements Drawable {

  class Point {

    float x;
    float y;
    Color color;
    float opacity;
    float size;

    Point(float x, float y, Color color, float opacity, float size) {
      this.x = x;
      this.y = y;
      this.color = new Color(color);
      this.opacity = opacity;
      this.size = size;
    }
  }

  private Color color = new Color(0);
  private float transparency = 255;
  private float size = 1;
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

  public float getColor() {
    return this.color.getHSB();
  }

  /**
   * Set color via hue value
   *
   * @param h a hue value [0...255]
   */
  public void setColor(float h) {
    this.color.setHSB(h);
  }

  public void setColor(double h) {
    this.setColor((float) h);
  }

  /**
   * Set color via rgb values
   *
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setColor(float r, float g, float b) {
    this.color.setRGB(r, g, b);
  }

  /**
   * Change color via a hue value, which is added to the current hue value
   *
   * @param c a hue value [0...255]
   */
  public void changeColor(float c) {
    this.color.changeColor(c);
  }

  public void changeColor(double c) {
    this.color.changeColor((float) c);
  }

  public void goToBackground() {
    this.isForeground = false;
  }

  public void goToForeground() {
    this.isForeground = true;
  }

  /**
   * Set the size of the pen
   *
   * @param size size of the pen
   */
  public void setSize(float size) {
    this.size = size;
  }

  /**
   * Returns the size of the pen
   *
   * @return the size of the pen
   */
  public float getSize() {
    return this.size;
  }

  /** Changes the size of the pen */
  public void changeSize(float size) {
    this.size += size;
  }

  /**
   * Set the transparency
   *
   * @param transparency transparency of the pen
   */
  public void setTransparency(float transparency) {
    this.transparency = transparency;
  }

  public void changeTransparency(float step) {
    this.setTransparency((this.transparency + step) % 255);
  }

  /**
   * Set the position if the pen is down.
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void setPosition(float x, float y) {
    if (this.down) {
      if (this.pointsBuffer.isEmpty()) {
        this.pointsBuffer.add(new CopyOnWriteArrayList<>());
      }
      this.pointsBuffer
          .get(this.pointsBuffer.size() - 1)
          .add(new Point(x, y, this.color, this.transparency, this.size));
    }
  }

  public void setPosition(double x, double y) {
    this.setPosition((float) x, (float) y);
  }

  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  public float getX() {
    return 0;
  }

  public float getY() {
    return 0;
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
    if (this.isForeground) {
      buffer = this.stage.getForegroundBuffer();
    }
    int pointsBufferSize = this.pointsBuffer.size();
    if (pointsBufferSize <= 0) return;

    Iterator<CopyOnWriteArrayList<Point>> pointsBufferIter = this.pointsBuffer.iterator();

    buffer.beginDraw();
    buffer.translate(this.stage.getWidth() / 2, this.stage.getHeight() / 2);

    while (pointsBufferIter.hasNext()) {
      CopyOnWriteArrayList<Point> points = pointsBufferIter.next();
      Iterator<Point> pointsIter = points.iterator();

      while (pointsIter.hasNext()) {
        Point point = pointsIter.next();
        if (this.previousPoint != null) {
          buffer.stroke(
              point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
          buffer.strokeWeight(point.size);
          buffer.line(this.previousPoint.x, -this.previousPoint.y, point.x, -point.y);
        } else if (this.previousPoint == null && !this.down) {
          buffer.stroke(
              point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
          buffer.fill(
              point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
          buffer.strokeWeight(point.size);
          buffer.circle(point.x, -point.y, point.size);
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
    buffer.endDraw();
  }
}
