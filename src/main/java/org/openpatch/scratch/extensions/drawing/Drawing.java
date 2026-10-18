package org.openpatch.scratch.extensions.drawing;

import java.util.Objects;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.internal.Image;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * The canvas passed to {@link DrawingSprite#draw(Drawing)}. Coordinates are
 * relative to the sprite: x points right, y points up, and images are centred
 * on their position. Images use the library's existing asset cache. Keep game
 * projection and depth ordering in your game, then draw the resulting 2D view.
 * The canvas is valid only during that draw call.
 *
   * @example.files DrawingSpriteExample.java
 */
public final class Drawing {
  private final PGraphics buffer;
  private final float opacity;
  private boolean active = true;

  Drawing(PGraphics buffer, double transparency) {
    this.buffer = buffer;
    this.opacity = (float) (255 * (1 - transparency / 100));
  }

  private void requireActive() {
    if (!this.active) throw new IllegalStateException("Draw only inside draw(Drawing). The previous frame's canvas has expired.");
  }

  void finish() { this.active = false; }

  /**
   * Draws a filled polygon with at least three points. Concave polygons are
   * supported; crossing outlines are not.
   * @param x x coordinates
   * @param y y coordinates (same length as x)
   * @param color fill colour
   *
   * @example.files DrawingSpriteExample.java
   */
  public void polygon(double[] x, double[] y, Color color) {
    this.requireActive();
    Objects.requireNonNull(x, "x coordinates");
    Objects.requireNonNull(y, "y coordinates");
    Objects.requireNonNull(color, "fill colour");
    if (x.length < 3 || x.length != y.length) {
      throw new IllegalArgumentException("A polygon needs equally many x/y coordinates and at least three points.");
    }
    for (int i = 0; i < x.length; i++) finite(x[i], y[i]);
    this.buffer.noStroke();
    this.buffer.fill((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), this.opacity);
    this.buffer.beginShape();
    for (int i = 0; i < x.length; i++) this.buffer.vertex((float) x[i], (float) -y[i]);
    this.buffer.endShape(PConstants.CLOSE);
  }

  /**
   * Draws a filled rectangle centred on (x,y).
   * @param x centre x
   * @param y centre y
   * @param width width in pixels
   * @param height height in pixels
   * @param color fill colour
   *
   * @example.files DrawingSpriteExample.java
   */
  public void rectangle(double x, double y, double width, double height, Color color) {
    dimensions(x, y, width, height);
    this.polygon(new double[] {x - width / 2, x + width / 2, x + width / 2, x - width / 2},
        new double[] {y - height / 2, y - height / 2, y + height / 2, y + height / 2}, color);
  }

  /**
   * Draws an image at its original size. A built-in costume name also works.
   * @param path image file or built-in costume
   * @param x centre x
   * @param y centre y
   *
   * @example.files DrawingSpriteExample.java
   */
  public void image(String path, double x, double y) {
    this.requireActive();
    PImage image = Image.loadImageOrBuiltin(path);
    this.image(path, x, y, image.width, image.height);
  }

  /**
   * Draws an image at the requested size, preserving its source in the cache.
   * @param path image file or built-in costume
   * @param x centre x
   * @param y centre y
   * @param width drawn width in pixels
   * @param height drawn height in pixels
   *
   * @example.files DrawingSpriteExample.java
   */
  public void image(String path, double x, double y, double width, double height) {
    this.requireActive();
    dimensions(x, y, width, height);
    PImage image = Image.loadImageOrBuiltin(path);
    this.buffer.imageMode(PConstants.CENTER);
    this.buffer.tint(255, this.opacity);
    this.buffer.image(image, (float) x, (float) -y, (float) width, (float) height);
    this.buffer.noTint();
  }

  /** @param path image file or built-in costume
   *
   * @return its original width in pixels
   *
   * @example.files DrawingSpriteExample.java
   */
  public int imageWidth(String path) { this.requireActive(); return Image.loadImageOrBuiltin(path).width; }

  /** @param path image file or built-in costume
   *
   * @return its original height in pixels
   *
   * @example.files DrawingSpriteExample.java
   */
  public int imageHeight(String path) { this.requireActive(); return Image.loadImageOrBuiltin(path).height; }

  /**
   * Limits subsequent drawing to a centred rectangle. This replaces the previous
   * clip; it is always cleared when the sprite finishes drawing. For a rotated
   * sprite, the clip is the screen-aligned bounding box of this rectangle.
   * @param x centre x
   * @param y centre y
   * @param width clip width
   * @param height clip height
   *
   * @example.files DrawingSpriteExample.java
   */
  public void clip(double x, double y, double width, double height) {
    this.requireActive();
    dimensions(x, y, width, height);
    // Processing's clip() ignores the model transform. Project all four corners
    // so sprite movement, rotation, size and the stage camera also move the clip.
    float left = Float.POSITIVE_INFINITY, top = Float.POSITIVE_INFINITY;
    float right = Float.NEGATIVE_INFINITY, bottom = Float.NEGATIVE_INFINITY;
    for (int i = 0; i < 4; i++) {
      float px = (float) (x + ((i & 1) == 0 ? -width / 2 : width / 2));
      float py = (float) (-y + ((i & 2) == 0 ? -height / 2 : height / 2));
      float sx = this.buffer.screenX(px, py), sy = this.buffer.screenY(px, py);
      left = Math.min(left, sx);
      right = Math.max(right, sx);
      top = Math.min(top, sy);
      bottom = Math.max(bottom, sy);
    }
    left = Math.max(0, Math.min(this.buffer.width, left));
    right = Math.max(left, Math.min(this.buffer.width, right));
    top = Math.max(0, Math.min(this.buffer.height, top));
    bottom = Math.max(top, Math.min(this.buffer.height, bottom));
    this.buffer.imageMode(PConstants.CORNER);
    this.buffer.clip(left, top, right - left, bottom - top);
  }

  /** Removes this sprite's clip.
   * @example.files DrawingSpriteExample.java
   */
  public void noClip() {
    this.requireActive();
    this.buffer.noClip();
  }

  private static void dimensions(double x, double y, double width, double height) {
    finite(x, y, width, height);
    if (width < 0 || height < 0) throw new IllegalArgumentException("Drawing dimensions cannot be negative.");
  }

  private static void finite(double... values) {
    for (double value : values) {
      if (!Double.isFinite(value)) throw new IllegalArgumentException("Drawing coordinates must be finite.");
    }
  }
}
