package org.openpatch.scratch.extensions.color;

/**
 * The Color class represents a color in the Scratch environment. It supports various
 * functionalities such as setting color values in the RGB and HSB spectrum, changing the color
 * based on a hue value, and converting between RGB and HSB color codes.
 */
public class Color {

  private double r = 255;
  private double g = 255;
  private double b = 255;

  private double h = 255;
  private double s = 255;
  private double l = 255;

  /** Constructs a new Color object with default values. */
  public Color() {}

  public Color(String hexCode) {
    if (hexCode.startsWith("#")) {
      hexCode = hexCode.substring(1);
    }
    var r = Integer.valueOf(hexCode.substring(0, 2), 16);
    var g = Integer.valueOf(hexCode.substring(2, 4), 16);
    var b = Integer.valueOf(hexCode.substring(4, 6), 16);
    this.setRGB(r, g, b);
  }

  /**
   * Constructs a new Color object with the specified hue value.
   *
   * @param h the hue value
   */
  public Color(double h) {
    this.setHSB(h);
  }

  /**
   * Constructs a new Color object with the specified rgb values.
   *
   * @param r the red value
   * @param g the green value
   * @param b the blue value
   */
  public Color(double r, double g, double b) {
    this.setRGB(r, g, g);
  }

  /**
   * Copies the received ScratchColor object.
   *
   * @param c ScratchColor object to copy
   */
  public Color(Color c) {
    this.r = c.r;
    this.g = c.g;
    this.b = c.b;

    this.h = c.h;
    this.s = c.s;
    this.l = c.l;
  }

  /**
   * Get the color value as a hex code. This is useful for comparing pixels.
   *
   * @return hex code
   */
  public int get() {
    var v1 = this.r;
    var v2 = this.g;
    var v3 = this.b;

    if (v1 > 255) v1 = 255;
    else if (v1 < 0) v1 = 0;
    if (v2 > 255) v2 = 255;
    else if (v2 < 0) v2 = 0;
    if (v3 > 255) v3 = 255;
    else if (v3 < 0) v3 = 0;

    return 0xff000000 | ((int) v1 << 16) | ((int) v2 << 8) | (int) v3;
  }

  /**
   * Get the color value on the HSB spectrum.
   *
   * @return hue value [0...255]
   */
  public double getHSB() {
    return this.h;
  }

  /**
   * Setting the color value after the HSB spectrum. Saturation and Luminosity are fixed at 255.
   *
   * @param h A hue value [0...255]
   */
  public void setHSB(double h) {
    this.setHSB(h, this.s, this.l);
  }

  /**
   * Setting the color value after the HSB spectrum.
   *
   * @param h A hue value [0...255]
   * @param s A saturation value [0...255]
   * @param l A luminosity value [0...255]
   */
  public void setHSB(double h, double s, double l) {
    while (h > 255) {
      h -= 255;
    }
    while (s > 255) {
      s -= 255;
    }
    while (l > 255) {
      l -= 255;
    }

    this.h = h;
    this.s = s;
    this.l = l;

    h = h / 255.0f;
    s = s / 255.0f;
    l = l / 255.0f;

    double[] rgb = Color.HSBtoRGB(h, s, l);
    this.r = rgb[0];
    this.g = rgb[1];
    this.b = rgb[2];
  }

  /**
   * Setting the color value after the RGB spectrum
   *
   * @param r A red value [0...255]
   * @param g A green value [0...255]
   * @param b A blue value [0...255]
   */
  public void setRGB(double r, double g, double b) {
    this.r = r;
    this.g = g;
    this.b = b;

    double[] hsb = Color.RGBtoHSB(r, g, b);

    this.h = hsb[0] * 255;
    this.s = hsb[1] * 255;
    this.l = hsb[2] * 255;
  }

  /**
   * Changes the color accordining to a hue value, which is added to the current hue value. When the
   * resulting value is greater than 255 it will be reset. For example: 285 => 30.
   *
   * @param h A hue value. Could be any positive or negative number.
   */
  public void changeColor(double h) {
    double newH = this.getH() + h;
    this.setHSB(newH);
  }

  /**
   * Transforms a given rgb color code to hsb
   *
   * @param r A red value [0...255]
   * @param g A green value [0...255]
   * @param b A blue value [0...255]
   * @return hsb values
   */
  private static double[] RGBtoHSB(double r, double g, double b) {
    var hsb =
        java.awt.Color.RGBtoHSB(
            Math.round((float) r), Math.round((float) g), Math.round((float) b), null);
    double[] hsbd = {hsb[0], hsb[1], hsb[2]};
    return hsbd;
  }

  /**
   * Transforms a given hsb color code to rgb
   *
   * @param h A hue value [0...1]
   * @param s A saturation value [0...1]
   * @param l A luminosity value [0...1]
   * @return rgb values
   */
  private static double[] HSBtoRGB(double h, double s, double l) {
    java.awt.Color colorRgb =
        new java.awt.Color(java.awt.Color.HSBtoRGB((float) h, (float) s, (float) l));
    double[] rgb = new double[3];
    rgb[0] = colorRgb.getRed();
    rgb[1] = colorRgb.getGreen();
    rgb[2] = colorRgb.getBlue();

    return rgb;
  }

  /**
   * Get the red value.
   *
   * @return red value [0...255]
   */
  public double getRed() {
    return this.r;
  }

  /**
   * Get the green value.
   *
   * @return green value [0...255]
   */
  public double getGreen() {
    return this.g;
  }

  /**
   * Get the blue value.
   *
   * @return blue value [0...255]
   */
  public double getBlue() {
    return this.b;
  }

  /**
   * Get the hue value.
   *
   * @return hue value [0...255]
   */
  public double getH() {
    return h;
  }

  /**
   * Get the saturation value.
   *
   * @return saturation value [0...255]
   */
  public double getS() {
    return s;
  }

  /**
   * Get the luminosity value.
   *
   * @return luminosity value [0...255]
   */
  public double getL() {
    return l;
  }
}
