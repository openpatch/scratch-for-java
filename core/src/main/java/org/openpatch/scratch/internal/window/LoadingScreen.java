package org.openpatch.scratch.internal.window;

import java.util.ArrayList;
import java.util.List;
import org.openpatch.scratch.internal.Assets;
import org.openpatch.scratch.internal.draw.CameraDraw;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.ImageDraw;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * The built-in loading screen as 6.x drew it: the logo (or {@code Window.useSplashLogo}'s picture),
 * the file being loaded and a percentage on dark grey, fading in and, once the assets are loaded
 * and a stage is set, out over that stage. Made of draw-list items, so the renderer needs nothing
 * of its own for it.
 */
final class LoadingScreen {

  static final double FADE_SECONDS = 0.3;
  private static final int BACKGROUND = 0xff222222;

  private final Image logo;
  private final double logoW, logoH;
  private long started;
  private long fadingOut;

  LoadingScreen(String logoPath, int stageW, int stageH) {
    Image picture = null;
    if (logoPath != null) {
      try {
        picture = Images.load(logoPath);
      } catch (RuntimeException e) {
        System.err.println("WARNING: Could not load the splash logo " + logoPath + ": " + e.getMessage());
      }
    }
    if (picture == null) {
      picture = Images.load("loading.png");
    }
    this.logo = picture;
    // into rather over half the stage, never enlarged
    double scale = Math.min(1, Math.min(stageW * 0.55 / picture.getWidth(), stageH * 0.55 / picture.getHeight()));
    this.logoW = picture.getWidth() * scale;
    this.logoH = picture.getHeight() * scale;
  }

  /**
   * Whether the screen is still shown at {@code now}; starts fading out once {@code ready}, but
   * only after the fade in finished, so a project with nothing to load still shows the logo arrive
   * and leave rather than blink.
   */
  boolean showing(long now, boolean ready) {
    if (this.started == 0) {
      this.started = now;
    }
    if (ready && this.fadingOut == 0 && seconds(now - this.started) >= FADE_SECONDS) {
      this.fadingOut = now;
    }
    return this.fadingOut == 0 || seconds(now - this.fadingOut) < FADE_SECONDS;
  }

  /** Whether the stage shows underneath (fading out). */
  boolean fadingOut() {
    return this.fadingOut != 0;
  }

  /** The screen alone, for a stage of this size. */
  DrawList alone(long now, int width, int height, Assets assets) {
    return new DrawList(-1, 0, width, height, BACKGROUND, null, CameraDraw.IDENTITY, List.of(), List.of(), List.of(),
        null, false, List.of(), List.of(), null, this.items(now, width, height, assets), null);
  }

  /** The screen on top of a stage's first draw list. */
  DrawList over(DrawList stage, long now, Assets assets) {
    List<Item> items = new ArrayList<>(stage.overlayItems());
    items.addAll(this.items(now, stage.width(), stage.height(), assets));
    return new DrawList(stage.stageId(), stage.tick(), stage.width(), stage.height(), stage.stageColor(),
        stage.backdrop(), stage.camera(), stage.layers(), stage.overlay(), stage.ui(), stage.ask(), stage.debug(),
        stage.stageShaders(), stage.monitors(), stage.transition(), items, stage.cursor());
  }

  private List<Item> items(long now, int width, int height, Assets assets) {
    double alpha = this.fadingOut != 0 ? 1 - seconds(now - this.fadingOut) / FADE_SECONDS
        : seconds(now - this.started) / FADE_SECONDS;
    alpha = Math.max(0, Math.min(1, alpha));
    int a = (int) Math.round(alpha * 255);
    List<Item> items = new ArrayList<>();
    if (this.fadingOut != 0) {
      double w = width / 2.0, h = height / 2.0;
      items.add(new ShapeDraw(new double[] {-w, h, w, h, w, -h, -w, -h}, (a << 24) | (BACKGROUND & 0xffffff), 0, 0));
    }
    double x = this.logoW / 2, y = this.logoH / 2;
    items.add(new ImageDraw(this.logo, 0, 0, this.logo.getWidth(), this.logo.getHeight(),
        new double[] {-x, y, x, y, x, -y, -x, -y}, (float) alpha));
    if (assets == null) {
      return items; // a stage being made: no files, no percentage
    }
    String file = assets.current();
    if (file.length() > 48) {
      file = "..." + file.substring(file.length() - 45);
    }
    int percent = (int) Math.round(assets.progress() * 100);
    int color = (a << 24) | 0xffffff;
    double top = -y - 8;
    for (String line : new String[] {file, percent + "%"}) {
      if (!line.isEmpty()) {
        TextLayout layout = TextLayout.of(line, Font.defaultFont(), 14, 0);
        items.add(new TextDraw(layout, -layout.width() / 2, top, layout.width(), layout.height(), 1, color,
            TextDraw.Box.NONE, 0, 0, 0, 0));
      }
      top -= 20;
    }
    return items;
  }

  private static double seconds(long nanos) {
    return nanos / 1e9;
  }
}
