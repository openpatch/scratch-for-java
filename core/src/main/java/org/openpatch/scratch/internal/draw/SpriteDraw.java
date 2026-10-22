package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.image.Image;

/**
 * A costume on the stage. The costume's pixel {@code (originX, originY)} (its rotation centre,
 * y down) lands on {@code (x, y)}; around that point the costume is scaled by
 * {@code scaleX, scaleY} (a negative {@code scaleX} mirrors it, for the left-right rotation style)
 * and turned clockwise by {@code rotation} degrees.
 *
 * @param costume the image, shared and never changed after loading; renderers may key textures
 *     on its identity
 * @param nineSlice for a sprite drawn at a set width and height: the four insets (left, top,
 *     right, bottom) in costume pixels whose corners keep their size; null otherwise
 * @param sliceScale how big the insets are drawn, per costume pixel: the sprite's size / 100
 * @param width the width to stretch to with {@code nineSlice}, in stage pixels
 * @param height the height to stretch to with {@code nineSlice}, in stage pixels
 * @param shaders the sprite's active shaders in order, with their uniform values; empty for none
 */
public record SpriteDraw(
    Image costume,
    double x,
    double y,
    double originX,
    double originY,
    double scaleX,
    double scaleY,
    double rotation,
    Effects effects,
    int[] nineSlice,
    double sliceScale,
    double width,
    double height,
    java.util.List<ShaderDraw> shaders) implements Item {

  /**
   * Where one axis of a nine-sliced costume is cut, as 6.x's UISprite cut it: the insets keep
   * their size (times {@code scale}), the middle stretches over the rest, even when it is empty
   * in the costume (a bar as high as its two caps). Drawn smaller than its insets, the insets
   * shrink together instead of folding over each other.
   *
   * @param costume the costume's width (or height) in pixels
   * @param start the inset at the left (or top), in costume pixels
   * @param end the inset at the right (or bottom), in costume pixels
   * @param drawn the drawn width (or height)
   * @param scale drawn size of a costume pixel of the insets
   * @return the drawn positions of the cuts and their texture coordinates (0–1), each from 0 to
   *     the end; one piece when the insets do not fit into the costume
   */
  public static double[][] slices(int costume, int start, int end, double drawn, double scale) {
    if (start < 0 || end < 0 || start + end > costume || costume <= 0) {
      return new double[][] {{0, drawn}, {0, 1}};
    }
    double a = start * scale, b = end * scale;
    if (a + b > drawn) {
      double f = a + b > 0 ? drawn / (a + b) : 0;
      a *= f;
      b *= f;
    }
    return new double[][] {
        {0, a, drawn - b, drawn},
        {0, start / (double) costume, (costume - end) / (double) costume, 1}};
  }
}
