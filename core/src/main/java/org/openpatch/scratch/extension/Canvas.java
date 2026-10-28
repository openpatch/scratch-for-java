package org.openpatch.scratch.extension;

import java.util.ArrayList;
import java.util.List;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.internal.draw.ClipDraw;
import org.openpatch.scratch.internal.draw.ImageDraw;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * What a layer drawer draws with: it records into the draw list, nothing is drawn at once and no
 * OpenGL is exposed. Coordinates are those of the layer: world coordinates (Scratch's, y up) for
 * the world layers, stage coordinates for the UI layer and the overlay. Game thread only, and only
 * during the drawer's call.
 */
public final class Canvas {

  /**
   * How the core makes canvases and takes their items. Registered by {@link CoreVersion}, which
   * the core always loads: a static initializer here would rely on Class.forName running it, which
   * the browser build does not.
   */
  static final class Hook implements org.openpatch.scratch.internal.CanvasAccess.Access {
    @Override
    public Canvas create() {
      return new Canvas();
    }

    @Override
    public List<Item> finish(Canvas canvas) {
      canvas.closed = true;
      return canvas.items;
    }
  }

  private final List<Item> items = new ArrayList<>();
  private float opacity = 1;
  private boolean closed;

  private Canvas() {}

  private void open() {
    if (this.closed) {
      throw new IllegalStateException("Draw only while the drawer is called; this canvas has expired.");
    }
  }

  /** @param opacity how opaque what follows is drawn, 0 to 1 */
  public void setOpacity(double opacity) {
    this.opacity = (float) Math.max(0, Math.min(1, opacity));
  }

  /**
   * An image (a file or a built-in sprite name), centred on a point.
   *
   * @param path the image
   * @param x x of its centre
   * @param y y of its centre
   * @param width its width
   * @param height its height
   */
  public void drawImage(String path, double x, double y, double width, double height) {
    Image image = Images.load(path);
    this.drawImageRegion(path, 0, 0, image.getWidth(), image.getHeight(), x, y, width, height);
  }

  /**
   * A region of an image, centred on a point: one tile of a tileset, one glyph of a bitmap font.
   *
   * @param path the image
   * @param sx the region's left edge in image pixels
   * @param sy its top edge
   * @param sw its width
   * @param sh its height
   * @param x x of the centre
   * @param y y of the centre
   * @param width the width to draw it at
   * @param height the height to draw it at
   */
  public void drawImageRegion(String path, int sx, int sy, int sw, int sh, double x, double y, double width,
      double height) {
    double l = x - width / 2, r = x + width / 2, t = y + height / 2, b = y - height / 2;
    this.drawImageQuad(path, sx, sy, sw, sh, new double[] {l, t, r, t, r, b, l, b});
  }

  /**
   * A region of an image on any quad: turned, mirrored, sheared.
   *
   * @param path the image
   * @param sx the region's left edge in image pixels
   * @param sy its top edge
   * @param sw its width
   * @param sh its height
   * @param corners where the region's top-left, top-right, bottom-right and bottom-left corners go:
   *     {@code x0, y0, …, x3, y3}
   */
  public void drawImageQuad(String path, int sx, int sy, int sw, int sh, double[] corners) {
    this.open();
    if (corners.length != 8) {
      throw new IllegalArgumentException("A quad has four corners: 8 numbers.");
    }
    this.items.add(new ImageDraw(Images.load(path), sx, sy, sw, sh, corners.clone(), this.opacity));
  }

  /**
   * A filled polygon, concave ones included.
   *
   * @param xs x of every corner
   * @param ys y of every corner, in the same order
   * @param fill its colour
   */
  public void drawPolygon(double[] xs, double[] ys, Color fill) {
    this.open();
    if (xs.length < 3 || xs.length != ys.length) {
      throw new IllegalArgumentException("A polygon needs equally many x and y and at least three corners.");
    }
    this.items.add(new ShapeDraw(points(xs, ys), this.withOpacity(fill), 0, 0));
  }

  /**
   * The outline of a polygon.
   *
   * @param xs x of every corner
   * @param ys y of every corner
   * @param stroke the outline's colour
   * @param weight the outline's width
   */
  public void drawOutline(double[] xs, double[] ys, Color stroke, double weight) {
    this.open();
    this.items.add(new ShapeDraw(points(xs, ys), 0, this.withOpacity(stroke), weight));
  }

  /**
   * A filled rectangle centred on a point.
   *
   * @param x x of its centre
   * @param y y of its centre
   * @param width its width
   * @param height its height
   * @param fill its colour
   */
  public void drawRectangle(double x, double y, double width, double height, Color fill) {
    this.drawPolygon(new double[] {x - width / 2, x + width / 2, x + width / 2, x - width / 2},
        new double[] {y + height / 2, y + height / 2, y - height / 2, y - height / 2}, fill);
  }

  /**
   * A line of text whose first baseline starts at a point, in the default font.
   *
   * @param text the text
   * @param x where it starts
   * @param y its baseline
   * @param size the font size
   * @param color the colour of the letters
   */
  public void drawText(String text, double x, double y, double size, Color color) {
    this.open();
    TextLayout layout = TextLayout.of(text, Font.defaultFont(), size, 0);
    this.items.add(new TextDraw(layout, x, y + layout.font().ascent(size), layout.width(), layout.height(), 0,
        this.withOpacity(color), TextDraw.Box.NONE, 0, 0, 0, 0));
  }

  /**
   * Draws only inside a rectangle (axis-aligned on the screen) until {@link #noClip()}.
   *
   * @param x x of its centre
   * @param y y of its centre
   * @param width its width
   * @param height its height
   */
  public void clip(double x, double y, double width, double height) {
    this.open();
    this.items.add(new ClipDraw(true, x - width / 2, y - height / 2, x + width / 2, y + height / 2));
  }

  /** Draws everywhere again. */
  public void noClip() {
    this.open();
    this.items.add(ClipDraw.END);
  }

  /**
   * @param path an image or a built-in sprite name
   * @return its width in pixels
   */
  public int imageWidth(String path) {
    return Images.load(path).getWidth();
  }

  /**
   * @param path an image or a built-in sprite name
   * @return its height in pixels
   */
  public int imageHeight(String path) {
    return Images.load(path).getHeight();
  }

  private int withOpacity(Color c) {
    return c.withAlpha(c.getAlpha() * this.opacity).toARGB();
  }

  private static double[] points(double[] xs, double[] ys) {
    double[] p = new double[xs.length * 2];
    for (int i = 0; i < xs.length; i++) {
      if (!Double.isFinite(xs[i]) || !Double.isFinite(ys[i])) {
        throw new IllegalArgumentException("Coordinates must be finite numbers.");
      }
      p[2 * i] = xs[i];
      p[2 * i + 1] = ys[i];
    }
    return p;
  }
}
