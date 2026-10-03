package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.List;

import org.openpatch.scratch.extensions.camera.Camera;
import org.openpatch.scratch.internal.Image;

import processing.core.PGraphics;

/**
 * Answers Scratch's "touching color?" and "color is touching color?" for the
 * sprites of one stage.
 *
 * <p>
 * Reading the rendered frame back from the graphics card would be the obvious
 * way, but it costs a full-frame copy and a stall per question - 8 MB on a
 * 1920 x 1080 stage - and the frame shows the asking sprite on top of exactly
 * the pixels it wants to look under. So this does what Scratch does instead:
 * it walks the screen pixels the sprite covers and, for each one the sprite
 * paints, works out on the CPU what the other layers show there. The cost
 * follows the size of the sprite, not the size of the stage.
 *
 * <p>
 * The layers, from the top: the foreground pen layer, the other sprites in the
 * order they are drawn, the background pen layer, the backdrop and the stage's
 * colour. Costumes and the backdrop are images the CPU already holds. The pen
 * layers live on the graphics card; they are copied back at the end of a frame
 * only once a sprite has asked about colours and only when something was drawn
 * on them since the last copy, so a stage whose pens stay idle never pays for
 * it.
 *
 * <p>
 * Texts, speech bubbles, UI sprites and shaders are not seen.
 */
final class ColorSensing {

  private final Stage stage;

  /** Set by the first question; until then the pen layers are not copied. */
  private volatile boolean wanted;

  private boolean backgroundStale;
  private boolean foregroundStale;
  private volatile int[] backgroundPixels;
  private volatile int[] foregroundPixels;

  ColorSensing(Stage stage) {
    this.stage = stage;
  }

  /** Whether a sprite has asked about colours, so the pen layers are worth watching. */
  boolean isWanted() {
    return this.wanted;
  }

  /**
   * Called by the stage once it has drawn its pen layers for a frame.
   *
   * @param background        the background pen layer
   * @param backgroundChanged whether anything was drawn on it or it was erased
   * @param foreground        the foreground pen layer
   * @param foregroundChanged the same for the foreground
   */
  void penLayersDrawn(PGraphics background, boolean backgroundChanged,
      PGraphics foreground, boolean foregroundChanged) {
    this.backgroundStale |= backgroundChanged;
    this.foregroundStale |= foregroundChanged;
    if (!this.wanted) {
      return;
    }
    if (this.backgroundStale) {
      this.backgroundPixels = copyOf(background);
      this.backgroundStale = false;
    }
    if (this.foregroundStale) {
      this.foregroundPixels = copyOf(foreground);
      this.foregroundStale = false;
    }
  }

  private static int[] copyOf(PGraphics layer) {
    layer.loadPixels();
    return layer.pixels == null ? null : layer.pixels.clone();
  }

  /**
   * Whether a painted pixel of {@code sprite} lies over {@code target}.
   *
   * @param sprite the asking sprite
   * @param mine   only pixels of the sprite of this colour count, or null for
   *               all of them
   * @param target the colour to look for under the sprite
   * @return true at the first pixel that matches
   */
  boolean isTouching(Sprite sprite, Color mine, Color target) {
    if (!this.wanted) {
      // The pen layers are copied from the next frame on; one frame without
      // them is the price of not copying them for stages that never ask.
      this.wanted = true;
      this.backgroundStale = true;
      this.foregroundStale = true;
    }

    Image costume = sprite.getCurrentCostume();
    if (costume == null || !sprite.isVisible() || sprite.isUI()) {
      return false;
    }

    View view = new View(this.stage);
    // The sprite's own transparency does not stop it from sensing, as in Scratch.
    Picture self = Picture.ofSprite(sprite, costume, view, false);
    int[] box = self.screenBox(view);
    if (box == null) {
      return false;
    }
    Picture[] others = this.othersOver(sprite, box, view);
    Picture backdrop = Picture.ofBackdrop(this.stage.getCurrentBackdrop(), view);
    int[] foreground = this.foregroundPixels;
    int[] background = this.backgroundPixels;
    Color stageColour = this.stage.getColor();
    double stageR = stageColour.getRed();
    double stageG = stageColour.getGreen();
    double stageB = stageColour.getBlue();

    int targetKey = key(target);
    int mineKey = mine == null ? 0 : key(mine);

    for (int py = box[1]; py < box[3]; py++) {
      double sy = py + 0.5;
      for (int px = box[0]; px < box[2]; px++) {
        double sx = px + 0.5;
        int own = self.sample(sx, sy);
        if ((own >>> 24) == 0 || (mine != null && key(own) != mineKey)) {
          continue;
        }

        // What the stage shows here without this sprite, laid from the top
        // down: each layer only shows through what the layers above left.
        int index = py * view.width + px;
        int top = this.topmostOpaque(foreground, index, others, background, backdrop, sx, sy);
        if (top != 0) {
          // The usual case: the first thing under the sprite hides the rest,
          // so its colour is the answer and nothing has to be mixed.
          if (key(top) == targetKey) {
            return true;
          }
          continue;
        }
        double r = 0, g = 0, b = 0, covered = 0;
        int c = foreground == null ? 0 : foreground[index];
        if ((c >>> 24) != 0) {
          double a = (c >>> 24) / 255.0;
          r += a * ((c >> 16) & 0xff);
          g += a * ((c >> 8) & 0xff);
          b += a * (c & 0xff);
          covered += a;
        }
        for (int i = 0; i < others.length && covered < 0.999; i++) {
          c = others[i].sample(sx, sy);
          if ((c >>> 24) != 0) {
            double a = (c >>> 24) / 255.0 * (1 - covered);
            r += a * ((c >> 16) & 0xff);
            g += a * ((c >> 8) & 0xff);
            b += a * (c & 0xff);
            covered += a;
          }
        }
        if (covered < 0.999 && background != null) {
          c = background[index];
          if ((c >>> 24) != 0) {
            double a = (c >>> 24) / 255.0 * (1 - covered);
            r += a * ((c >> 16) & 0xff);
            g += a * ((c >> 8) & 0xff);
            b += a * (c & 0xff);
            covered += a;
          }
        }
        if (covered < 0.999 && backdrop != null) {
          c = backdrop.sample(sx, sy);
          if ((c >>> 24) != 0) {
            double a = (c >>> 24) / 255.0 * (1 - covered);
            r += a * ((c >> 16) & 0xff);
            g += a * ((c >> 8) & 0xff);
            b += a * (c & 0xff);
            covered += a;
          }
        }
        if (covered < 0.999) {
          double a = 1 - covered;
          r += a * stageR;
          g += a * stageG;
          b += a * stageB;
        }

        if (key((clamp(r) << 16) | (clamp(g) << 8) | clamp(b)) == targetKey) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * The colour of the topmost layer at a point if that layer is fully opaque
   * there and nothing above it is partly transparent, otherwise 0 - then the
   * layers have to be mixed.
   */
  private int topmostOpaque(int[] foreground, int index, Picture[] others, int[] background,
      Picture backdrop, double sx, double sy) {
    int c = foreground == null ? 0 : foreground[index];
    if ((c >>> 24) != 0) {
      return (c >>> 24) == 0xff ? c : 0;
    }
    for (Picture other : others) {
      c = other.sample(sx, sy);
      if ((c >>> 24) != 0) {
        return (c >>> 24) == 0xff ? c : 0;
      }
    }
    c = background == null ? 0 : background[index];
    if ((c >>> 24) != 0) {
      return (c >>> 24) == 0xff ? c : 0;
    }
    c = backdrop == null ? 0 : backdrop.sample(sx, sy);
    if ((c >>> 24) != 0) {
      return (c >>> 24) == 0xff ? c : 0;
    }
    Color colour = this.stage.getColor();
    return 0xff000000 | (clamp(colour.getRed()) << 16)
        | (clamp(colour.getGreen()) << 8) | clamp(colour.getBlue());
  }

  /** The sprites other than {@code self} that cover part of the box, top first. */
  private Picture[] othersOver(Sprite self, int[] box, View view) {
    Object[] all = this.stage.sprites.toArray();
    List<Picture> over = new ArrayList<>();
    for (int i = all.length - 1; i >= 0; i--) {
      Sprite other = (Sprite) all[i];
      if (other == self || !other.isVisible() || other.isUI()) {
        continue;
      }
      Image costume = other.getCurrentCostume();
      if (costume == null) {
        continue;
      }
      Picture picture = Picture.ofSprite(other, costume, view, true);
      int[] b = picture.screenBox(view);
      if (b == null || b[2] <= box[0] || b[0] >= box[2] || b[3] <= box[1] || b[1] >= box[3]) {
        continue;
      }
      over.add(picture);
    }
    return over.toArray(new Picture[0]);
  }

  /**
   * Scratch's tolerance: red and green compared on their top five bits, blue
   * on its top four, so anti-aliased edges and nearly-equal colours still
   * count.
   */
  private static int key(int argb) {
    return argb & 0xf8f8f0;
  }

  private static int key(Color c) {
    return key((clamp(c.getRed()) << 16) | (clamp(c.getGreen()) << 8) | clamp(c.getBlue()));
  }

  private static int clamp(double v) {
    return (int) Math.max(0, Math.min(255, Math.round(v)));
  }

  /** How world coordinates map onto the stage's pixels this frame. */
  private static final class View {
    final int width;
    final int height;
    final double zoom;
    final double cameraX;
    final double cameraY;

    View(Stage stage) {
      Camera camera = stage.getCamera();
      this.width = stage.getWidth();
      this.height = stage.getHeight();
      this.zoom = camera.getZoom() / 100.0;
      this.cameraX = camera.getX();
      this.cameraY = camera.getY();
    }
  }

  /**
   * An image where it is drawn: which pixel of it lies under a point of the
   * stage, with its tint and transparency applied. The point is mapped to the
   * image's pixels by one linear map, worked out once, so asking costs a few
   * multiplications.
   */
  private static final class Picture {
    final int[] pixels;
    final int pixelsWide;
    final int pixelsHigh;
    // pixel column = ux * sx + uy * sy + u0, row = vx * sx + vy * sy + v0
    final double ux;
    final double uy;
    final double u0;
    final double vx;
    final double vy;
    final double v0;
    final double tintR;
    final double tintG;
    final double tintB;
    final double opacity;
    // the image's corners on the stage, for its bounding box
    final double[] cornersX = new double[4];
    final double[] cornersY = new double[4];

    private Picture(Image image, double[] map, boolean withTransparency) {
      this.pixels = image.getOriginalPixels();
      this.pixelsWide = image.getOriginalWidth();
      this.pixelsHigh = image.getOriginalHeight();
      this.ux = map[0];
      this.uy = map[1];
      this.u0 = map[2];
      this.vx = map[3];
      this.vy = map[4];
      this.v0 = map[5];
      Color tint = image.getTint();
      this.tintR = tint.getRed() / 255;
      this.tintG = tint.getGreen() / 255;
      this.tintB = tint.getBlue() / 255;
      this.opacity = withTransparency ? 1 - image.getTransparency() / 100 : 1;
    }

    /** A costume as Image.draw places it: turned or mirrored about the sprite. */
    static Picture ofSprite(Sprite sprite, Image costume, View view, boolean withTransparency) {
      RotationStyle style = sprite.getRotationStyle();
      double angle = style == RotationStyle.ALL_AROUND
          ? Math.toRadians(sprite.getDirection() - 90)
          : 0;
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double mirror = Image.isMirrored(sprite.getDirection(), style) ? -1 : 1;
      double w = costume.getWidth();
      double h = costume.getHeight();
      double x = sprite.getX();
      double y = sprite.getY();
      double scaleU = costume.getOriginalWidth() / w;
      double scaleV = costume.getOriginalHeight() / h;
      double[] center = sprite.getDrawnRotationCenter(costume);

      // A stage point back in the costume's own frame (y down, centred), then
      // in its pixels. Every step is linear, so three points pin the map down.
      double[] at = new double[6];
      double[][] points = { { 0, 0 }, { 1, 0 }, { 0, 1 } };
      double[] us = new double[3];
      double[] vs = new double[3];
      for (int i = 0; i < 3; i++) {
        double dx = (points[i][0] - view.width / 2.0) / view.zoom + view.cameraX - x;
        double dy = (points[i][1] - view.height / 2.0) / view.zoom - view.cameraY + y;
        double lx = (dx * cos + dy * sin) * mirror;
        double ly = -dx * sin + dy * cos;
        us[i] = (lx + center[0]) * scaleU;
        vs[i] = (ly + center[1]) * scaleV;
      }
      at[0] = us[1] - us[0];
      at[1] = us[2] - us[0];
      at[2] = us[0];
      at[3] = vs[1] - vs[0];
      at[4] = vs[2] - vs[0];
      at[5] = vs[0];
      Picture picture = new Picture(costume, at, withTransparency);

      for (int corner = 0; corner < 4; corner++) {
        double lx = (corner % 2 == 0 ? 0 : w) - center[0];
        double ly = (corner < 2 ? 0 : h) - center[1];
        lx *= mirror;
        double dx = lx * cos - ly * sin;
        double dy = lx * sin + ly * cos;
        picture.cornersX[corner] = view.width / 2.0 + view.zoom * (x + dx - view.cameraX);
        picture.cornersY[corner] = view.height / 2.0 + view.zoom * (-y + dy + view.cameraY);
      }
      return picture;
    }

    /** The backdrop as Stage draws it: stretched over the stage, moved by the camera. */
    static Picture ofBackdrop(Image backdrop, View view) {
      if (backdrop == null) {
        return null;
      }
      double scaleU = backdrop.getOriginalWidth() / (double) view.width;
      double scaleV = backdrop.getOriginalHeight() / (double) view.height;
      double[] map = {
          scaleU / view.zoom, 0, view.cameraX * scaleU,
          0, scaleV / view.zoom, -view.cameraY * scaleV };
      return new Picture(backdrop, map, true);
    }

    /** The screen pixels the image can cover, clipped to the stage, or null. */
    int[] screenBox(View view) {
      double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
      for (int i = 0; i < 4; i++) {
        minX = Math.min(minX, this.cornersX[i]);
        minY = Math.min(minY, this.cornersY[i]);
        maxX = Math.max(maxX, this.cornersX[i]);
        maxY = Math.max(maxY, this.cornersY[i]);
      }
      int left = Math.max(0, (int) Math.floor(minX));
      int top = Math.max(0, (int) Math.floor(minY));
      int right = Math.min(view.width, (int) Math.ceil(maxX));
      int bottom = Math.min(view.height, (int) Math.ceil(maxY));
      if (left >= right || top >= bottom) {
        return null;
      }
      return new int[] { left, top, right, bottom };
    }

    /** The image's colour at a stage point, transparent where it paints nothing. */
    int sample(double sx, double sy) {
      double u = this.ux * sx + this.uy * sy + this.u0;
      double v = this.vx * sx + this.vy * sy + this.v0;
      if (u < 0 || v < 0 || u >= this.pixelsWide || v >= this.pixelsHigh) {
        return 0;
      }
      int c = this.pixels[(int) v * this.pixelsWide + (int) u];
      int a = c >>> 24;
      if (a == 0) {
        return 0;
      }
      if (this.opacity < 1) {
        a = (int) (a * this.opacity);
      }
      return (a << 24)
          | ((int) (((c >> 16) & 0xff) * this.tintR) << 16)
          | ((int) (((c >> 8) & 0xff) * this.tintG) << 8)
          | (int) ((c & 0xff) * this.tintB);
    }
  }
}
