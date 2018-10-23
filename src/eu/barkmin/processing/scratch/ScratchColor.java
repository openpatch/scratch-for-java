package eu.barkmin.processing.scratch;

import java.awt.Color;

public class ScratchColor {
  public static float RGBtoHSB(float r, float g, float b) {
    float[] hsb = Color.RGBtoHSB(Math.round(r),Math.round(g),Math.round(b),null);
    return hsb[0] * 255;
  }
  
  public static float[] HSBtoRGB(float h) {
    h = (h % 255) / 255;
    Color colorRgb = new Color(Color.HSBtoRGB(h, 1, 1));
    float[] rgb = new float[3];
    rgb[0] = colorRgb.getRed();
    rgb[1] = colorRgb.getGreen();
    rgb[2] = colorRgb.getBlue();
    
    return rgb;
  }
}
