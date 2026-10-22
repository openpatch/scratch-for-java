package org.openpatch.scratch.drawing;

import java.util.Objects;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.extension.Canvas;

/**
 * What a {@link DrawingSprite} draws with in {@code draw(Drawing)}: coordinates are the sprite's
 * own, with (0, 0) at its position and y up; they turn, mirror and grow with the sprite. Only
 * valid during that call.
 */
public final class Drawing {

  private final Canvas canvas;
  // sprite-local → layer coordinates: x' = a·x + c·y + e, y' = b·x + d·y + f
  private final double a, b, c, d, e, f;

  Drawing(Canvas canvas, DrawingSprite sprite) {
    this.canvas = canvas;
    double s = sprite.getSize() / 100;
    double sx = s, sy = s, r = 0;
    if (sprite.getRotationStyle() == RotationStyle.ALL_AROUND) {
      r = Math.toRadians(sprite.getDirection() - 90);
    } else if (sprite.getRotationStyle() == RotationStyle.LEFT_RIGHT && sprite.getDirection() > 180) {
      sx = -s;
    }
    // turned clockwise, as sprites turn
    double cos = Math.cos(r), sin = Math.sin(r);
    this.a = sx * cos;
    this.b = -sx * sin;
    this.c = sy * sin;
    this.d = sy * cos;
    this.e = sprite.getX();
    this.f = sprite.getY();
    canvas.setOpacity(1 - sprite.getTransparency() / 100);
  }

  private double x(double x, double y) {
    return this.a * x + this.c * y + this.e;
  }

  private double y(double x, double y) {
    return this.b * x + this.d * y + this.f;
  }

  /**
   * A filled polygon.
   *
   * @param x x of every corner
   * @param y y of every corner
   * @param color the fill
   *
   * @example.files DrawingSpriteExample.java
   */
  public void polygon(double[] x, double[] y, Color color) {
    Objects.requireNonNull(color, "fill colour");
    if (x.length < 3 || x.length != y.length) {
      throw new IllegalArgumentException("A polygon needs equally many x/y coordinates and at least three points.");
    }
    double[] px = new double[x.length], py = new double[y.length];
    for (int i = 0; i < x.length; i++) {
      px[i] = this.x(x[i], y[i]);
      py[i] = this.y(x[i], y[i]);
    }
    this.canvas.drawPolygon(px, py, color);
  }

  /**
   * A filled rectangle centred on a point.
   *
   * @param x x of its centre
   * @param y y of its centre
   * @param width its width
   * @param height its height
   * @param color the fill
   *
   * @example.files DrawingSpriteExample.java
   */
  public void rectangle(double x, double y, double width, double height, Color color) {
    this.polygon(new double[] {x - width / 2, x + width / 2, x + width / 2, x - width / 2},
        new double[] {y + height / 2, y + height / 2, y - height / 2, y - height / 2}, color);
  }

  /**
   * An image at its own size, centred on a point.
   *
   * @param path an image file or a built-in sprite name
   * @param x x of its centre
   * @param y y of its centre
   *
   * @example.files DrawingSpriteExample.java
   */
  public void image(String path, double x, double y) {
    this.image(path, x, y, this.canvas.imageWidth(path), this.canvas.imageHeight(path));
  }

  /**
   * An image stretched to a size, centred on a point.
   *
   * @param path an image file or a built-in sprite name
   * @param x x of its centre
   * @param y y of its centre
   * @param width the width
   * @param height the height
   *
   * @example.files DrawingSpriteExample.java
   */
  public void image(String path, double x, double y, double width, double height) {
    double l = x - width / 2, r = x + width / 2, t = y + height / 2, bo = y - height / 2;
    this.canvas.drawImageQuad(path, 0, 0, this.canvas.imageWidth(path), this.canvas.imageHeight(path),
        new double[] {this.x(l, t), this.y(l, t), this.x(r, t), this.y(r, t), this.x(r, bo), this.y(r, bo),
            this.x(l, bo), this.y(l, bo)});
  }

  /**
   * @param path an image file or a built-in sprite name
   * @return its width in pixels
   *
   * @example.files DrawingSpriteExample.java
   */
  public int imageWidth(String path) {
    return this.canvas.imageWidth(path);
  }

  /**
   * @param path an image file or a built-in sprite name
   * @return its height in pixels
   *
   * @example.files DrawingSpriteExample.java
   */
  public int imageHeight(String path) {
    return this.canvas.imageHeight(path);
  }

  /**
   * Draws only inside a rectangle until {@link #noClip()}; a turned sprite clips to the rectangle
   * around the turned one.
   *
   * @param x x of its centre
   * @param y y of its centre
   * @param width its width
   * @param height its height
   *
   * @example.files DrawingSpriteExample.java
   */
  public void clip(double x, double y, double width, double height) {
    double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < 4; i++) {
      double lx = x + ((i & 1) == 0 ? -width / 2 : width / 2), ly = y + ((i & 2) == 0 ? -height / 2 : height / 2);
      minX = Math.min(minX, this.x(lx, ly));
      maxX = Math.max(maxX, this.x(lx, ly));
      minY = Math.min(minY, this.y(lx, ly));
      maxY = Math.max(maxY, this.y(lx, ly));
    }
    this.canvas.clip((minX + maxX) / 2, (minY + maxY) / 2, maxX - minX, maxY - minY);
  }

  /**
   * Draws everywhere again.
   *
   * @example.files DrawingSpriteExample.java
   */
  public void noClip() {
    this.canvas.noClip();
  }
}
