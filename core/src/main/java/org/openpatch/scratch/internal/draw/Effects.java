package org.openpatch.scratch.internal.draw;

/**
 * The graphic effects of a sprite. {@link #apply(int)} is the definition: the CPU (stamps,
 * colour sensing) uses it directly, and the renderer's shader does the same maths.
 *
 * @param colorEffect Scratch's colour effect: shifts the hue by {@code colorEffect / 200} of a
 *     turn (0 = none, wraps at 200)
 * @param transparency Scratch's ghost effect in percent (0 = opaque, 100 = invisible)
 * @param tint ARGB colour the costume is multiplied by ({@code 0xffffffff} = none)
 */
public record Effects(double colorEffect, double transparency, int tint) {

  public static final Effects NONE = new Effects(0, 0, 0xffffffff);

  /**
   * Scratch's own thresholds (sprite.frag in scratch-render): colours darker or greyer than this
   * are forced to carry a little hue, so the colour effect stays visible on white and black.
   */
  private static final double MIN_LIGHTNESS = 0.11 / 2;

  private static final double MIN_SATURATION = 0.09;

  public boolean isNone() {
    return this.colorEffect % 200 == 0 && this.transparency == 0 && this.tint == 0xffffffff;
  }

  /** A costume pixel (straight ARGB) with the effects applied, in the order hue, tint, ghost. */
  public int apply(int argb) {
    int a = argb >>> 24;
    if (a == 0 || this.isNone()) {
      return argb;
    }
    int r = (argb >> 16) & 0xff, g = (argb >> 8) & 0xff, b = argb & 0xff;
    double shift = this.colorEffect % 200;
    if (shift != 0) {
      int[] rgb = shiftHue(r, g, b, shift / 200);
      r = rgb[0];
      g = rgb[1];
      b = rgb[2];
    }    if (this.tint != 0xffffffff) {
      r = r * ((this.tint >> 16) & 0xff) / 255;
      g = g * ((this.tint >> 8) & 0xff) / 255;
      b = b * (this.tint & 0xff) / 255;
      a = a * (this.tint >>> 24) / 255;
    }
    if (this.transparency != 0) {
      double keep = 1 - Math.max(0, Math.min(100, this.transparency)) / 100;
      a = (int) Math.round(a * keep);
    }
    return a << 24 | r << 16 | g << 8 | b;
  }

  /**
   * Turns the hue by {@code turns} (0..1) in HSV, as Scratch does: values too dark or too grey to
   * show a hue are forced to carry a little, so the shift is visible on white and black too.
   */
  static int[] shiftHue(int r, int g, int b, double turns) {
    double rn = r / 255.0, gn = g / 255.0, bn = b / 255.0;
    double max = Math.max(rn, Math.max(gn, bn));
    double d = max - Math.min(rn, Math.min(gn, bn));
    double h;
    if (d == 0) {
      h = 0;
    } else if (max == rn) {
      h = ((((gn - bn) / d) % 6 + 6) % 6) / 6;
    } else if (max == gn) {
      h = (((bn - rn) / d) + 2) / 6;
    } else {
      h = (((rn - gn) / d) + 4) / 6;
    }
    double s = max == 0 ? 0 : d / max;
    double v = max;
    if (v < MIN_LIGHTNESS) {
      h = 0;
      s = 1;
      v = MIN_LIGHTNESS;
    } else if (s < MIN_SATURATION) {
      h = 0;
      s = MIN_SATURATION;
    }
    h = (h + turns) % 1;
    if (h < 0) {
      h += 1;
    }
    double hh = h * 6;
    int sector = (int) Math.floor(hh) % 6;
    double f = hh - Math.floor(hh);
    double p = v * (1 - s), q = v * (1 - s * f), t = v * (1 - s * (1 - f));
    double[] rgb = switch (sector) {
      case 0 -> new double[] {v, t, p};
      case 1 -> new double[] {q, v, p};
      case 2 -> new double[] {p, v, t};
      case 3 -> new double[] {p, q, v};
      case 4 -> new double[] {t, p, v};
      default -> new double[] {v, p, q};
    };
    return new int[] {(int) Math.round(rgb[0] * 255), (int) Math.round(rgb[1] * 255),
        (int) Math.round(rgb[2] * 255)};
  }
}
