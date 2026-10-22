package org.openpatch.scratch.internal.compose;

import java.util.List;
import org.openpatch.scratch.internal.canvas.Canvas;
import org.openpatch.scratch.internal.draw.Effects;
import org.openpatch.scratch.internal.image.Image;

/**
 * The one CPU compositor behind colour sensing and {@code Pixels}: what
 * the world layers show at a stage pixel, the same on every GPU, in the same tick, without a
 * window. Shaders, texts and the UI layer are not part of it.
 *
 * <p>Built for one question from the scene as it is at that statement, back to front: the stage
 * colour, then {@link Part}s (the backdrop, canvases, sprites). Coordinates are screen coordinates
 * (Scratch's: origin in the centre, y up) after the camera; pixel {@code (px, py)} of the stage
 * (y down) is sampled at its centre.
 */
public final class Compositor {

  /** Something the compositor stacks: a costume on the screen, or a canvas. */
  public sealed interface Part permits Picture, CanvasPart {}

  /** A layer's canvas, fixed on screen. */
  public record CanvasPart(Canvas canvas) implements Part {}

  /**
   * A costume through an affine map from costume pixels (origin top left, y down) to screen
   * coordinates, with effects.
   */
  public static final class Picture implements Part {
    private final int[] pixels;
    private final int width, height;
    private final double ia, ib, ic, id, ie, iff;
    private final Effects effects;
    // no effects to apply: decided once, not for every sampled pixel
    private final boolean plain;
    private final double[][] cutsU, cutsV;
    final double minX, minY, maxX, maxY;

    /**
     * @param image the costume
     * @param costumeToScreen {a, b, c, d, e, f}: {@code x' = a·u + c·v + e}, {@code y' = b·u + d·v + f}
     * @param effects applied to every sampled pixel
     */
    public Picture(Image image, double[] costumeToScreen, Effects effects) {
      this(image, costumeToScreen, effects, null, null);
    }

    /**
     * A nine-sliced costume: {@code costumeToScreen} stretches the costume evenly, the cuts then
     * move each sampled point to the costume pixel the renderer shows there.
     *
     * @param cutsU across: the cuts in evenly stretched costume pixels and the costume pixels they
     *     show, as {@code {positions, pixels}}; null for none
     * @param cutsV the same downwards
     */
    public Picture(Image image, double[] costumeToScreen, Effects effects, double[][] cutsU, double[][] cutsV) {
      this.pixels = image.getArgb();
      this.width = image.getWidth();
      this.height = image.getHeight();
      this.effects = effects;
      this.plain = effects.isNone();
      this.cutsU = cutsU;
      this.cutsV = cutsV;
      double a = costumeToScreen[0], b = costumeToScreen[1], c = costumeToScreen[2], d = costumeToScreen[3];
      double e = costumeToScreen[4], f = costumeToScreen[5];
      double det = a * d - b * c;
      if (det == 0) {
        // shrunk to nothing: covers no pixel
        this.ia = this.ib = this.ic = this.id = this.ie = this.iff = 0;
        this.minX = this.minY = Double.POSITIVE_INFINITY;
        this.maxX = this.maxY = Double.NEGATIVE_INFINITY;
        return;
      }
      // screen → costume
      this.ia = d / det;
      this.ib = -b / det;
      this.ic = -c / det;
      this.id = a / det;
      this.ie = (c * f - d * e) / det;
      this.iff = (b * e - a * f) / det;
      double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
      for (int corner = 0; corner < 4; corner++) {
        double u = (corner & 1) * this.width, v = (corner >> 1) * this.height;
        double x = a * u + c * v + e, y = b * u + d * v + f;
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
      }
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
    }

    /** The costume pixel (effects applied) under a screen point, or transparent. */
    public int sample(double x, double y) {
      if (x < this.minX || x > this.maxX || y < this.minY || y > this.maxY) {
        return 0;
      }
      double u = this.ia * x + this.ic * y + this.ie, v = this.ib * x + this.id * y + this.iff;
      if (u < 0 || v < 0 || u >= this.width || v >= this.height) {
        return 0;
      }
      int pu, pv;
      if (this.cutsU == null && this.cutsV == null) {
        pu = (int) u;
        pv = (int) v;
      } else {
        pu = cut(this.cutsU, u, this.width);
        pv = cut(this.cutsV, v, this.height);
      }
      int pixel = this.pixels[pv * this.width + pu];
      return this.plain ? pixel : this.effects.apply(pixel);
    }

    /** The costume pixel shown at an evenly stretched position: piecewise linear between cuts. */
    private static int cut(double[][] cuts, double at, int size) {
      double p = at;
      if (cuts != null) {
        double[] from = cuts[0], to = cuts[1];
        int i = 0;
        while (i + 2 < from.length && at >= from[i + 1]) {
          i++;
        }
        double span = from[i + 1] - from[i];
        p = span > 0 ? to[i] + (at - from[i]) / span * (to[i + 1] - to[i]) : to[i];
      }
      return Math.max(0, Math.min(size - 1, (int) p));
    }

    boolean overlapsBox(double minX, double minY, double maxX, double maxY) {
      return this.maxX >= minX && this.minX <= maxX && this.maxY >= minY && this.minY <= maxY;
    }
  }

  private final int width, height;
  private final int background;
  private final List<Part> parts;

  /**
   * @param width the stage width
   * @param height the stage height
   * @param stageColor ARGB behind everything (made opaque)
   * @param backToFront the parts, back to front
   */
  public Compositor(int width, int height, int stageColor, List<Part> backToFront) {
    this.width = width;
    this.height = height;
    this.background = stageColor | 0xff000000;
    this.parts = backToFront;
  }

  /** The opaque colour the world layers show at stage pixel {@code (px, py)} (y down). */
  public int colorAt(int px, int py) {
    double x = px + 0.5 - this.width / 2.0, y = this.height / 2.0 - (py + 0.5);
    return this.composite(this.parts, px, py, x, y);
  }

  /** Every stage pixel, top row first: what {@code Pixels.of(stage)} returns. */
  public int[] render() {
    int[] out = new int[this.width * this.height];
    for (int py = 0; py < this.height; py++) {
      for (int px = 0; px < this.width; px++) {
        out[py * this.width + px] = this.colorAt(px, py);
      }
    }
    return out;
  }

  /**
   * Scratch's "touching colour" and "colour is touching colour": does any pixel of {@code self}
   * (whose colour matches {@code mine}, if given) lie over a pixel of the other parts whose
   * colour matches {@code target}? {@code self} must not be among the parts.
   *
   * @param self the sensing sprite, its own ghost effect left out
   * @param mine ARGB the sprite's pixel must match, or null for any painted pixel
   * @param target ARGB to look for underneath
   */
  public boolean touches(Picture self, Integer mine, int target) {
    int targetKey = key(target);
    int mineKey = mine == null ? 0 : key(mine);
    int left = Math.max(0, (int) Math.floor(self.minX + this.width / 2.0));
    int right = Math.min(this.width - 1, (int) Math.ceil(self.maxX + this.width / 2.0));
    int top = Math.max(0, (int) Math.floor(this.height / 2.0 - self.maxY));
    int bottom = Math.min(this.height - 1, (int) Math.ceil(this.height / 2.0 - self.minY));
    if (left > right || top > bottom) {
      return false;
    }
    // only the parts near the sprite matter
    List<Part> near = this.parts.stream()
        .filter(p -> !(p instanceof Picture pic) || pic.overlapsBox(self.minX, self.minY, self.maxX, self.maxY))
        .toList();
    for (int py = top; py <= bottom; py++) {
      double y = this.height / 2.0 - (py + 0.5);
      for (int px = left; px <= right; px++) {
        double x = px + 0.5 - this.width / 2.0;
        int own = self.sample(x, y);
        if ((own >>> 24) == 0 || (mine != null && key(own) != mineKey)) {
          continue;
        }
        if (key(this.composite(near, px, py, x, y)) == targetKey) {
          return true;
        }
      }
    }
    return false;
  }

  /** Front to back with coverage, as the screen shows it; opaque result. */
  private int composite(List<Part> parts, int px, int py, double x, double y) {
    double r = 0, g = 0, b = 0, covered = 0;
    for (int i = parts.size() - 1; i >= 0 && covered < 0.999; i--) {
      Part part = parts.get(i);
      int c = part instanceof Picture pic ? pic.sample(x, y) : ((CanvasPart) part).canvas().get(px, py);
      int alpha = c >>> 24;
      if (alpha == 0) {
        continue;
      }
      double a = alpha / 255.0 * (1 - covered);
      r += a * ((c >> 16) & 0xff);
      g += a * ((c >> 8) & 0xff);
      b += a * (c & 0xff);
      covered += a;
    }
    if (covered < 0.999) {
      double a = 1 - covered;
      r += a * ((this.background >> 16) & 0xff);
      g += a * ((this.background >> 8) & 0xff);
      b += a * (this.background & 0xff);
    }
    return 0xff000000 | clamp(r) << 16 | clamp(g) << 8 | clamp(b);
  }

  /** Scratch's colour tolerance: the top 5 bits of red and green, 4 of blue. */
  public static int key(int argb) {
    return argb & 0xf8f8f0;
  }

  private static int clamp(double v) {
    return (int) Math.max(0, Math.min(255, Math.round(v)));
  }
}
