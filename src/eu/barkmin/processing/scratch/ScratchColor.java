package eu.barkmin.processing.scratch;

import java.awt.Color;

public class ScratchColor {

    private float r = 255;
    private float g = 255;
    private float b = 255;

    private float h = 255;
    private float s = 255;
    private float l = 255;

    public ScratchColor() {
    }

    public ScratchColor(float h) {
        this.setHSB(h);
    }

    /**
     * Copies the received ScratchColor object.
     *
     * @param c ScratchColor object to copy
     */
    public ScratchColor(ScratchColor c) {
        this.r = c.r;
        this.g = c.g;
        this.b = c.b;

        this.h = c.h;
        this.s = c.s;
        this.l = c.l;
    }

    /**
     * Get the color value on the HSB spectrum.
     *
     * @return hue value [0...255]
     */
    public float getHSB() {
        return this.h;
    }

    /**
     * Setting the color value after the HSB spectrum. Saturation and Luminosity are fixed at 255.
     *
     * @param h A hue value [0...255]
     */
    public void setHSB(float h) {
        this.setHSB(h, this.s, this.l);
    }

    /**
     * Setting the color value after the HSB spectrum.
     *
     * @param h A hue value [0...255]
     * @param s A saturation value [0...255]
     * @param l A luminosity value [0...255]
     */
    public void setHSB(float h, float s, float l) {
        while (h > 255) {
            h -= 255;
        }
        while (s > 255) {
            s-= 255;
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

        float[] rgb = ScratchColor.HSBtoRGB(h, s, l);
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
    public void setRGB(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;

        float[] hsb = ScratchColor.RGBtoHSB(r, g, b);

        this.h = hsb[0] * 255;
        this.s = hsb[1] * 255;
        this.l = hsb[2] * 255;
    }

    /**
     * Changes the color accordining to a hue value, which is added to the current hue value. When the resulting value is greater than 255 it will be reset. For example: 285 => 30.
     *
     * @param h A hue value. Could be any positive or negative number.
     */
    public void changeColor(float h) {
        float newH = this.getHSB() + h;
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
    private static float[] RGBtoHSB(float r, float g, float b) {
        float[] hsb = Color.RGBtoHSB(Math.round(r), Math.round(g), Math.round(b), null);
        return hsb;
    }

    /**
     * Transforms a given hsb color code to rgb
     *
     * @param h A hue value [0...1]
     * @param s A saturation value [0...1]
     * @param l A luminosity value [0...1]
     * @return rgb values
     */
    private static float[] HSBtoRGB(float h, float s, float l) {
        Color colorRgb = new Color(Color.HSBtoRGB(h, s, l));
        float[] rgb = new float[3];
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
    public float getRed() {
        return this.r;
    }

    /**
     * Get the green value.
     *
     * @return green value [0...255]
     */
    public float getGreen() {
        return this.g;
    }

    /**
     * Get the blue value.
     *
     * @return blue value [0...255]
     */
    public float getBlue() {
        return this.b;
    }


    public float getH() {
        return h;
    }

    public float getS() {
        return s;
    }

    public float getL() {
        return l;
    }
}
