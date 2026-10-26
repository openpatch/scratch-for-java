package org.openpatch.scratch;

/**
 * A colour. Colours never change: {@code withHue(…)} and the other {@code with} methods return a
 * new colour, so a colour can be shared, kept in a constant ({@link HtmlColor}) or handed to
 * several sprites without one of them changing it for the others.
 *
 * <p>A colour can be given in two ways:
 *
 * <ul>
 *   <li>red, green and blue from 0 to 255, plus an optional alpha (opacity) from 0 to 255:
 *       {@code new Color(255, 128, 0)};
 *   <li>Scratch's colour, saturation and brightness, each from 0 to 100:
 *       {@code Color.fromHSB(50, 100, 100)}. The colour (hue) goes around: 0 is red, about 17
 *       yellow, 33 green, 50 cyan, 67 blue, 83 magenta, 100 red again.
 * </ul>
 *
 * <pre>{@code
 * Color orange = new Color(255, 128, 0);
 * Color lighter = orange.withBrightness(100).withSaturation(50);
 * Color next = orange.withHue(orange.getHue() + 10);
 * }</pre>
 */
public final class Color {

  private final double red, green, blue, alpha;

  /**
   * A colour from red, green and blue, each from 0 to 255 (values outside are clamped). The
   * colour is opaque.
   *
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   */
  public Color(double red, double green, double blue) {
    this(red, green, blue, 255);
  }

  /**
   * A colour from red, green, blue and alpha, each from 0 to 255 (values outside are clamped).
   * An alpha of 0 is invisible, 255 opaque.
   *
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   * @param alpha opacity from 0 to 255
   */
  public Color(double red, double green, double blue, double alpha) {
    this.red = clamp(red, 255);
    this.green = clamp(green, 255);
    this.blue = clamp(blue, 255);
    this.alpha = clamp(alpha, 255);
  }

  /**
   * A colour from a hex code as in HTML and CSS: {@code "#ff8000"}, {@code "ff8000"},
   * {@code "#f80"}, or with alpha {@code "#ff800080"}.
   *
   * @param hex the hex code
   */
  public Color(String hex) {
    int[] rgba = parseHex(hex);
    this.red = rgba[0];
    this.green = rgba[1];
    this.blue = rgba[2];
    this.alpha = rgba[3];
  }

  /**
   * A colour from Scratch's colour, saturation and brightness, each from 0 to 100. The colour
   * goes around (110 is the same as 10); saturation and brightness are clamped.
   *
   * @param hue the colour from 0 to 100
   * @param saturation from 0 (grey) to 100 (full colour)
   * @param brightness from 0 (black) to 100 (full brightness)
   * @return the colour
   */
  public static Color fromHSB(double hue, double saturation, double brightness) {
    double h = wrap(hue, 100) / 100 * 6;
    double s = clamp(saturation, 100) / 100;
    double v = clamp(brightness, 100) / 100;
    int sector = (int) Math.floor(h) % 6;
    double f = h - Math.floor(h);
    double p = v * (1 - s), q = v * (1 - s * f), t = v * (1 - s * (1 - f));
    double r, g, b;
    switch (sector) {
      case 0 -> { r = v; g = t; b = p; }
      case 1 -> { r = q; g = v; b = p; }
      case 2 -> { r = p; g = v; b = t; }
      case 3 -> { r = p; g = q; b = v; }
      case 4 -> { r = t; g = p; b = v; }
      default -> { r = v; g = p; b = q; }
    }
    // whole channels, as Scratch keeps colours (6.x gave 252 here, not 251.99999999999977)
    return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
  }

  /**
   * A colour from a packed ARGB number, as {@link #toARGB()} gives it.
   *
   * @param argb alpha, red, green and blue in one int
   * @return the colour
   */
  public static Color fromARGB(int argb) {
    return new Color((argb >> 16) & 0xff, (argb >> 8) & 0xff, argb & 0xff, argb >>> 24);
  }

  /**
   * @return red from 0 to 255
   *
   * @example.files ColorGetRed.java
   */
  public double getRed() {
    return this.red;
  }

  /**
   * @return green from 0 to 255
   *
   * @example.files ColorGetGreen.java
   */
  public double getGreen() {
    return this.green;
  }

  /**
   * @return blue from 0 to 255
   *
   * @example.files ColorGetBlue.java
   */
  public double getBlue() {
    return this.blue;
  }

  /** @return alpha (opacity) from 0 (invisible) to 255 (opaque) */
  public double getAlpha() {
    return this.alpha;
  }

  /** @return Scratch's colour (hue) from 0 to 100; 0 for greys */
  public double getHue() {
    double max = Math.max(this.red, Math.max(this.green, this.blue));
    double min = Math.min(this.red, Math.min(this.green, this.blue));
    double d = max - min;
    if (d == 0) {
      return 0;
    }
    double h;
    if (max == this.red) {
      h = ((this.green - this.blue) / d) % 6;
    } else if (max == this.green) {
      h = (this.blue - this.red) / d + 2;
    } else {
      h = (this.red - this.green) / d + 4;
    }
    return wrap(h / 6 * 100, 100);
  }

  /** @return saturation from 0 (grey) to 100 */
  public double getSaturation() {
    double max = Math.max(this.red, Math.max(this.green, this.blue));
    double min = Math.min(this.red, Math.min(this.green, this.blue));
    return max == 0 ? 0 : (max - min) / max * 100;
  }

  /** @return brightness from 0 (black) to 100 */
  public double getBrightness() {
    return Math.max(this.red, Math.max(this.green, this.blue)) / 255 * 100;
  }

  /**
   * @param red red from 0 to 255
   * @return this colour with another red
   */
  public Color withRed(double red) {
    return new Color(red, this.green, this.blue, this.alpha);
  }

  /**
   * @param green green from 0 to 255
   * @return this colour with another green
   */
  public Color withGreen(double green) {
    return new Color(this.red, green, this.blue, this.alpha);
  }

  /**
   * @param blue blue from 0 to 255
   * @return this colour with another blue
   */
  public Color withBlue(double blue) {
    return new Color(this.red, this.green, blue, this.alpha);
  }

  /**
   * @param alpha opacity from 0 to 255
   * @return this colour with another alpha
   */
  public Color withAlpha(double alpha) {
    return new Color(this.red, this.green, this.blue, alpha);
  }

  /**
   * Another colour (hue), keeping saturation, brightness and alpha. The colour goes around, so
   * {@code withHue(getHue() + 10)} works like Scratch's "change color by 10".
   *
   * @param hue the colour from 0 to 100
   * @return this colour with another hue
   */
  public Color withHue(double hue) {
    return fromHSB(hue, this.getSaturation(), this.getBrightness()).withAlpha(this.alpha);
  }

  /**
   * @param saturation from 0 to 100
   * @return this colour with another saturation
   */
  public Color withSaturation(double saturation) {
    return fromHSB(this.getHue(), saturation, this.getBrightness()).withAlpha(this.alpha);
  }

  /**
   * @param brightness from 0 to 100
   * @return this colour with another brightness
   */
  public Color withBrightness(double brightness) {
    return fromHSB(this.getHue(), this.getSaturation(), brightness).withAlpha(this.alpha);
  }

  /**
   * The colour packed into one int as alpha, red, green and blue (8 bits each, rounded), the way
   * pixels are stored.
   *
   * @return the packed colour
   */
  public int toARGB() {
    return (int) Math.round(this.alpha) << 24 | (int) Math.round(this.red) << 16
        | (int) Math.round(this.green) << 8 | (int) Math.round(this.blue);
  }

  /**
   * @return {@code #rrggbb}, or {@code #rrggbbaa} when not opaque
   *
   * @example.files ColorToString.java
   */
  @Override
  public String toString() {
    int argb = this.toARGB();
    String rgb = String.format("#%06x", argb & 0xffffff);
    return (argb >>> 24) == 255 ? rgb : rgb + String.format("%02x", argb >>> 24);
  }

  /** Two colours are equal when they pack into the same ARGB number. */
  @Override
  public boolean equals(Object other) {
    return other instanceof Color c && c.toARGB() == this.toARGB();
  }

  @Override
  public int hashCode() {
    return this.toARGB();
  }

  private static double clamp(double value, double max) {
    return Double.isNaN(value) ? 0 : Math.max(0, Math.min(max, value));
  }

  private static double wrap(double value, double range) {
    double w = value % range;
    return w < 0 ? w + range : w;
  }

  private static int[] parseHex(String hex) {
    String h = hex.trim();
    if (h.startsWith("#")) {
      h = h.substring(1);
    }
    if (h.length() == 3 || h.length() == 4) {
      StringBuilder doubled = new StringBuilder();
      for (char c : h.toCharArray()) {
        doubled.append(c).append(c);
      }
      h = doubled.toString();
    }
    if (h.length() != 6 && h.length() != 8) {
      throw new IllegalArgumentException("Not a colour: \"" + hex + "\". Use a hex code such as \"#ff8000\".");
    }
    try {
      long value = Long.parseLong(h, 16);
      if (h.length() == 6) {
        return new int[] {(int) (value >> 16) & 0xff, (int) (value >> 8) & 0xff, (int) value & 0xff, 255};
      }
      return new int[] {(int) (value >> 24) & 0xff, (int) (value >> 16) & 0xff, (int) (value >> 8) & 0xff,
          (int) value & 0xff};
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Not a colour: \"" + hex + "\". Use a hex code such as \"#ff8000\".", e);
    }
  }
}
