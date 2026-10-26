package org.openpatch.scratch.internal.canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openpatch.scratch.internal.draw.CanvasRegion;
import org.openpatch.scratch.internal.draw.Effects;
import org.openpatch.scratch.internal.image.Image;

/**
 * A layer's canvas: stage-sized ARGB pixels on the CPU, top row first, straight alpha. Pen
 * strokes, stamps and erasing change it at once, in program order, so colour sensing sees exactly
 * what the statements before it drew. What changed since the last
 * {@link #takeChanges()} is copied into the draw list for upload.
 *
 * <p>Drawing methods take screen coordinates: Scratch's stage coordinates (origin in the centre, y
 * up) after the camera. Used on the game thread only.
 */
public final class Canvas {

  private final int width, height;
  private final int[] argb;
  private final List<CanvasRegion> changes = new ArrayList<>();
  private int changedX0 = Integer.MAX_VALUE, changedY0 = Integer.MAX_VALUE, changedX1 = -1, changedY1 = -1;

  public Canvas(int width, int height) {
    this.width = width;
    this.height = height;
    this.argb = new int[width * height];
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  /** The pixel at canvas pixel {@code (px, py)} (y down), or transparent outside. */
  public int get(int px, int py) {
    if (px < 0 || py < 0 || px >= this.width || py >= this.height) {
      return 0;
    }
    return this.argb[py * this.width + px];
  }

  /** The pixel under the screen point {@code (x, y)}, or transparent outside. */
  public int at(double x, double y) {
    return this.get((int) Math.floor(x + this.width / 2.0), (int) Math.floor(this.height / 2.0 - y));
  }

  /** A copy of all pixels, top row first. */
  public int[] copyPixels() {
    return this.argb.clone();
  }

  /** An anti-aliased line with round caps; {@code size} is its width in stage pixels. */
  public void line(double x0, double y0, double x1, double y1, double size, int color) {
    double ax = x0 + this.width / 2.0, ay = this.height / 2.0 - y0;
    double bx = x1 + this.width / 2.0, by = this.height / 2.0 - y1;
    double r = Math.max(size, 0.5) / 2;
    int left = Math.max(0, (int) Math.floor(Math.min(ax, bx) - r - 1));
    int right = Math.min(this.width - 1, (int) Math.ceil(Math.max(ax, bx) + r + 1));
    int top = Math.max(0, (int) Math.floor(Math.min(ay, by) - r - 1));
    int bottom = Math.min(this.height - 1, (int) Math.ceil(Math.max(ay, by) + r + 1));
    if (left > right || top > bottom) {
      return;
    }
    double dx = bx - ax, dy = by - ay, length2 = dx * dx + dy * dy;
    int alpha = color >>> 24;
    for (int py = top; py <= bottom; py++) {
      for (int px = left; px <= right; px++) {
        double cx = px + 0.5 - ax, cy = py + 0.5 - ay;
        double t = length2 == 0 ? 0 : Math.max(0, Math.min(1, (cx * dx + cy * dy) / length2));
        double distance = Math.hypot(cx - t * dx, cy - t * dy);
        double coverage = Math.max(0, Math.min(1, r + 0.5 - distance));
        if (coverage > 0) {
          int i = py * this.width + px;
          this.argb[i] = over(this.argb[i], color, (int) Math.round(alpha * coverage));
        }
      }
    }
    this.changed(left, top, right, bottom);
  }

  /** A round dot, as a pen leaves when it goes down without moving. */
  public void dot(double x, double y, double size, int color) {
    this.line(x, y, x, y, size, color);
  }

  /**
   * Draws a costume through an affine transform from costume pixels (y down, origin top left) to
   * screen coordinates: {@code x' = a·u + c·v + e}, {@code y' = b·u + d·v + f}. Each canvas pixel
   * takes the costume pixel under its centre (nearest), so stamps look like the sprite in pixel
   * art and smooth costumes stay sharp at size 100.
   */
  public void stamp(Image costume, double[] costumeToScreen, Effects effects) {
    double a = costumeToScreen[0], b = costumeToScreen[1], c = costumeToScreen[2];
    double d = costumeToScreen[3], e = costumeToScreen[4], f = costumeToScreen[5];
    double det = a * d - b * c;
    if (det == 0) {
      return;
    }
    int cw = costume.getWidth(), ch = costume.getHeight();
    // screen bounds of the costume's corners, as canvas pixels
    double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    for (int corner = 0; corner < 4; corner++) {
      double u = (corner & 1) * cw, v = (corner >> 1) * ch;
      double px = a * u + c * v + e + this.width / 2.0;
      double py = this.height / 2.0 - (b * u + d * v + f);
      minX = Math.min(minX, px);
      maxX = Math.max(maxX, px);
      minY = Math.min(minY, py);
      maxY = Math.max(maxY, py);
    }
    int left = Math.max(0, (int) Math.floor(minX)), right = Math.min(this.width - 1, (int) Math.ceil(maxX));
    int top = Math.max(0, (int) Math.floor(minY)), bottom = Math.min(this.height - 1, (int) Math.ceil(maxY));
    if (left > right || top > bottom) {
      return;
    }
    int[] pixels = costume.getArgb();
    for (int py = top; py <= bottom; py++) {
      double sy = this.height / 2.0 - (py + 0.5);
      for (int px = left; px <= right; px++) {
        double sx = px + 0.5 - this.width / 2.0;
        // inverse of the affine map: screen → costume
        double rx = sx - e, ry = sy - f;
        double u = (d * rx - c * ry) / det, v = (-b * rx + a * ry) / det;
        if (u < 0 || v < 0 || u >= cw || v >= ch) {
          continue;
        }
        int src = effects.apply(pixels[(int) v * cw + (int) u]);
        if ((src >>> 24) != 0) {
          int i = py * this.width + px;
          this.argb[i] = over(this.argb[i], src, src >>> 24);
        }
      }
    }
    this.changed(left, top, right, bottom);
  }

  /** Clears the whole canvas. */
  public void eraseAll() {
    Arrays.fill(this.argb, 0);
    // an erase replaces everything before it: earlier regions need not be uploaded
    this.changes.clear();
    this.changedX0 = this.changedY0 = Integer.MAX_VALUE;
    this.changedX1 = this.changedY1 = -1;
    this.changed(0, 0, this.width - 1, this.height - 1);
  }

  /** {@code color} with alpha {@code a} over {@code dst}, straight alpha. */
  static int over(int dst, int color, int a) {
    if (a <= 0) {
      return dst;
    }
    int da = dst >>> 24;
    if (a >= 255 || da == 0) {
      return Math.min(a, 255) << 24 | (color & 0xffffff);
    }
    int outA = a + da * (255 - a) / 255;
    int r = (((color >> 16) & 0xff) * a + ((dst >> 16) & 0xff) * da * (255 - a) / 255) / outA;
    int g = (((color >> 8) & 0xff) * a + ((dst >> 8) & 0xff) * da * (255 - a) / 255) / outA;
    int b = ((color & 0xff) * a + (dst & 0xff) * da * (255 - a) / 255) / outA;
    return outA << 24 | r << 16 | g << 8 | b;
  }

  private void changed(int x0, int y0, int x1, int y1) {
    this.changedX0 = Math.min(this.changedX0, x0);
    this.changedY0 = Math.min(this.changedY0, y0);
    this.changedX1 = Math.max(this.changedX1, x1);
    this.changedY1 = Math.max(this.changedY1, y1);
  }

  /** The next {@link #takeChanges()} hands out the whole canvas: the stage is shown again. */
  public void resendAll() {
    this.changes.clear();
    this.changedX0 = this.changedY0 = Integer.MAX_VALUE;
    this.changedX1 = this.changedY1 = -1;
    this.changed(0, 0, this.width - 1, this.height - 1);
  }

  /**
   * The regions changed since the last call, oldest first, as copies: one bounding rectangle of
   * everything drawn since the last erase (or the last call), plus the full canvas after an erase.
   */
  public List<CanvasRegion> takeChanges() {
    if (this.changedX1 >= 0) {
      int w = this.changedX1 - this.changedX0 + 1, h = this.changedY1 - this.changedY0 + 1;
      int[] copy = new int[w * h];
      for (int row = 0; row < h; row++) {
        System.arraycopy(this.argb, (this.changedY0 + row) * this.width + this.changedX0, copy, row * w, w);
      }
      this.changes.add(new CanvasRegion(this.changedX0, this.changedY0, w, h, copy));
      this.changedX0 = this.changedY0 = Integer.MAX_VALUE;
      this.changedX1 = this.changedY1 = -1;
    }
    List<CanvasRegion> taken = List.copyOf(this.changes);
    this.changes.clear();
    return taken;
  }
}
