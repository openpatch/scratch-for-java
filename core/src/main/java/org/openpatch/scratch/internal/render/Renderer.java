package org.openpatch.scratch.internal.render;

import static org.lwjgl.opengl.GL33C.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.openpatch.scratch.internal.draw.AskDraw;
import org.openpatch.scratch.internal.draw.CameraDraw;
import org.openpatch.scratch.internal.draw.ClipDraw;
import org.openpatch.scratch.internal.draw.ImageDraw;
import org.openpatch.scratch.internal.draw.CanvasRegion;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.Effects;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.LayerDraw;
import org.openpatch.scratch.internal.draw.MonitorDraw;
import org.openpatch.scratch.internal.draw.ShaderDraw;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.SpriteDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.draw.TransitionDraw;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.render.Gpu.Batch;
import org.openpatch.scratch.internal.render.Gpu.Target;
import org.openpatch.scratch.internal.render.Gpu.Texture;
import org.openpatch.scratch.internal.render.Programs.Program;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * Turns a draw list into pixels. It reads nothing but the draw list, creates
 * every GPU object lazily on the window thread, and never touches learner objects.
 *
 * <p>World layers (backdrop, BACKGROUND, MAIN, FOREGROUND) are drawn at stage resolution, 4×
 * multisampled in normal mode; the stage's shaders work on that picture; it is scaled into the
 * letterbox. Speech bubbles and world texts, the UI layer, UI texts and monitors follow at the
 * window's resolution, so they stay sharp.
 */
public final class Renderer {

  /** Where stage coordinates land on the target: pixel = offset + (stage + half size) × scale. */
  private record View(double offsetX, double offsetY, double scale, CameraDraw camera, int width, int height) {

    double px(double x) {
      double sx = this.camera == null ? x : (x - this.camera.x()) * this.camera.zoom() / 100;
      return this.offsetX + (sx + this.width / 2.0) * this.scale;
    }

    double py(double y) {
      double sy = this.camera == null ? y : (y - this.camera.y()) * this.camera.zoom() / 100;
      return this.offsetY + (this.height / 2.0 - sy) * this.scale;
    }

    /** Stage units to target pixels, the camera's zoom included. */
    double unit() {
      return this.scale * (this.camera == null ? 1 : this.camera.zoom() / 100);
    }
  }

  private final boolean pixelArt;
  // small costumes share textures, so sprites with different costumes are drawn together
  private final CostumeAtlas atlas;
  /**
   * Godot's "canvas items" way of showing pixel art: the world is drawn at the window's
   * resolution with the camera unsnapped, so motion is continuous instead of stepping whole
   * stage pixels. Sprites keep nearest-neighbour sampling ( {@code pixelArt} ), and captures
   * and stages with shaders still take the crisp low-resolution path below.
   */
  private final boolean smoothWorld;
  /** How much of the previous frame each presented one keeps (0 = off). See {@link #smooth}. */
  private final double smoothing;
  private Target smoothPicture, smoothPrevious;
  private boolean smoothHasPrevious;
  private int printedFbW = -1, printedFbH = -1;
  private final Batch batch;
  private final Program sprite, coverage;
  private final Map<String, Program> studentPrograms = new HashMap<>();
  private final Map<String, Program> transitionPrograms = new HashMap<>();
  private final Set<String> brokenShaders = new HashSet<>();
  private final Map<Image, Texture> textures = new WeakHashMap<>();
  /** Canvas textures per stage and layer; let go of when their stage has not been seen a while. */
  private final Map<Integer, Texture[]> canvases = new HashMap<>();
  private final Map<Integer, Long> stageLastSeen = new HashMap<>();
  /** Draw lists whose canvas changes were uploaded already (a list may be drawn several times). */
  private final Map<DrawList, Boolean> uploaded = new IdentityHashMap<>();
  private final GlyphAtlas glyphs = new GlyphAtlas();
  private final Texture white;
  private Target world, worldPing, worldPong, frameA, frameB, capture, captureFrom, captureTo;
  private long frames;
  private double fps;

  /** Call on the window thread with the OpenGL context current. */
  public Renderer(boolean pixelArt, boolean smoothPixelArt, double frameSmoothingPercent) {
    this.pixelArt = pixelArt;
    this.smoothWorld = smoothPixelArt;
    this.smoothing = Math.max(0, Math.min(100, frameSmoothingPercent)) / 100;
    this.batch = new Batch();
    this.sprite = Programs.link("sprite", Programs.VERTEX, Programs.SPRITE);
    this.coverage = Programs.link("coverage", Programs.VERTEX, Programs.COVERAGE);
    this.white = new Texture(1, 1, new int[] {0xffffffff}, false, false);
    this.atlas = new CostumeAtlas(!pixelArt);
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA); // premultiplied alpha
  }

  /** For the debug overlay: frames per second measured by the window. */
  public void setFps(double fps) {
    this.fps = fps;
  }

  /**
   * Draws a frame into the window's framebuffer, letterboxed.
   *
   * @param framebufferWidth the framebuffer's width in device pixels
   * @param framebufferHeight its height
   */
  public void render(DrawList list, int framebufferWidth, int framebufferHeight) {
    this.frames++;
    double[] frame = letterbox(list.width(), list.height(), framebufferWidth, framebufferHeight, this.pixelArt,
        this.smoothWorld);
    if (Boolean.getBoolean("scratch4j.debugGl")
        && (framebufferWidth != this.printedFbW || framebufferHeight != this.printedFbH)) {
      this.printedFbW = framebufferWidth;
      this.printedFbH = framebufferHeight;
      System.out.printf("Scratch for Java: letterbox stage %dx%d, framebuffer %dx%d: "
          + "offset (%.2f, %.2f), size %.2fx%.2f, scale %.4f%n",
          list.width(), list.height(), framebufferWidth, framebufferHeight,
          frame[0], frame[1], frame[2], frame[3], frame[4]);
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glViewport(0, 0, framebufferWidth, framebufferHeight);
    glClearColor(0, 0, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
    TransitionDraw transition = list.transition();
    if (transition == null || transition.from() == null) {
      this.renderStage(list, null, framebufferWidth, framebufferHeight, frame);
    } else {
      int w = (int) Math.round(frame[2]), h = (int) Math.round(frame[3]);
      this.frameA = this.sized(this.frameA, w, h, 1);
      this.frameB = this.sized(this.frameB, w, h, 1);
      double[] inner = {0, 0, w, h, frame[4]};
      this.renderStage(transition.from(), this.frameA, w, h, inner);
      this.renderStage(list, this.frameB, w, h, inner);
      glBindFramebuffer(GL_FRAMEBUFFER, 0);
      glViewport(0, 0, framebufferWidth, framebufferHeight);
      this.drawTransition(transition, this.frameA, this.frameB, framebufferWidth, framebufferHeight, frame, false);
      // frame smoothing starts afresh on the new stage, not from the old stage's last picture
      this.smoothHasPrevious = false;
    }
    this.forgetOldStages(list.stageId());
  }

  /**
   * The stage at stage size, overlay included and scaled down: what preview and capture save.
   * Straight ARGB, top row first.
   */
  public int[] capture(DrawList list) {
    return this.capture(list, false);
  }

  /**
   * @param withTransition true for the frame as the window shows it, a running transition
   *     included (recorders, {@code afterFrame}); false for the stage alone
   */
  public int[] capture(DrawList list, boolean withTransition) {
    int w = list.width(), h = list.height();
    double[] whole = {0, 0, w, h, 1};
    this.capture = this.sized(this.capture, w, h, 1);
    TransitionDraw transition = withTransition ? list.transition() : null;
    if (transition == null || transition.from() == null) {
      this.renderStage(list, this.capture, w, h, whole);
    } else {
      this.captureFrom = this.sized(this.captureFrom, w, h, 1);
      this.captureTo = this.sized(this.captureTo, w, h, 1);
      this.renderStage(transition.from(), this.captureFrom, w, h, whole);
      this.renderStage(list, this.captureTo, w, h, whole);
      this.capture.bind();
      clear(0xff000000);
      this.drawTransition(transition, this.captureFrom, this.captureTo, w, h, whole, true);
      this.capture.resolve();
    }
    return this.capture.read();
  }

  private boolean isCapture(Target out) {
    return out != null && (out == this.capture || out == this.captureFrom || out == this.captureTo);
  }

  /** Where the stage goes: {x, y, width, height, scale} in framebuffer pixels. */
  static double[] letterbox(int stageW, int stageH, int fbW, int fbH, boolean pixelArt, boolean smooth) {
    double scale = Math.min((double) fbW / stageW, (double) fbH / stageH);
    // whole numbers only in crisp pixel art; smooth pixel art scales to fit, like its camera zoom
    if (pixelArt && !smooth && scale >= 1) {
      scale = Math.floor(scale); // whole numbers only, the rest letterboxed
    }
    double w = stageW * scale, h = stageH * scale;
    return new double[] {Math.floor((fbW - w) / 2), Math.floor((fbH - h) / 2), w, h, scale};
  }

  // ---- one stage ------------------------------------------------------------------------

  /**
   * One stage into {@code out} (null: the window's framebuffer, already cleared), the letterbox
   * at {@code frame} = {x, y, width, height, scale}.
   */
  /**
   * The letterbox area only, cleared to the stage's colour, when the world is drawn straight into
   * the window. The rest of the framebuffer is already black (see {@link #render}).
   */
  private void clearLetterbox(double[] frame, int stageColor, int targetH) {
    int x = (int) frame[0];
    int y = (int) frame[1];
    int w = (int) Math.round(frame[2]), h = (int) Math.round(frame[3]);
    glEnable(GL_SCISSOR_TEST);
    glScissor(x, targetH - y - h, w, h);
    clear(stageColor);
    glDisable(GL_SCISSOR_TEST);
  }

  /**
   * In pixel-art mode the camera sits on whole stage pixels as well: the tiles and canvases drawn
   * through it are not snapped anywhere else, so an unrounded camera would leave the world's pixel
   * grid on half-pixel offsets whenever it follows something that moves fractionally. In smooth
   * pixel art nothing is snapped on purpose: continuous positions are the point.
   */
  private CameraDraw snap(CameraDraw camera) {
    if (!this.pixelArt || this.smoothWorld || camera == null) {
      return camera;
    }
    return new CameraDraw(Math.round(camera.x()), Math.round(camera.y()), camera.zoom());
  }

  private void renderStage(DrawList list, Target out, int targetW, int targetH, double[] frame) {
    boolean intoTarget = out != null;
    this.uploadCanvases(list);
    int w = list.width(), h = list.height();
    // smooth pixel art draws the world straight into the window, at its resolution: there is no
    // world picture to blend or shade (a stage with shaders takes the crisp path instead)
    boolean deviceWorld = !intoTarget && this.smoothWorld && list.stageShaders().isEmpty();
    Texture worldPicture = null;
    if (!deviceWorld) {
      // 1. the world layers at stage resolution
      this.world = this.sized(this.world, w, h, this.pixelArt ? 1 : 4);
      this.world.bind();
      clear(list.stageColor());
      View worldView = new View(0, 0, 1, this.snap(list.camera()), w, h);
      View fixed = new View(0, 0, 1, null, w, h);
      if (list.backdrop() != null) {
        this.drawSprite(list.backdrop(), worldView, w, h, true, list.tick());
        this.endSpriteRun();
      }
      for (LayerDraw layer : list.layers()) {
        if (!layer.world()) {
          continue;
        }
        this.drawCanvas(list.stageId(), layer.index(), fixed, w, h, true);
        this.drawItems(layer.items(), worldView, w, h, true, list.tick());
      }
      this.world.resolve();
      worldPicture = this.applyStageShaders(list, this.world);
      if (this.smoothing > 0 && !intoTarget) {
        // the window's own stage only: transitions draw two stages a frame, and captures stay
        // reproducible and leave the previous picture alone
        worldPicture = this.smooth(worldPicture, w, h);
      }
    }

    // 2. into the letterbox, then what is drawn at the target's resolution
    if (intoTarget) {
      out.bind();
      clear(0xff000000);
    } else {
      glBindFramebuffer(GL_FRAMEBUFFER, 0);
      glViewport(0, 0, targetW, targetH);
    }
    if (deviceWorld) {
      this.clearLetterbox(frame, list.stageColor(), targetH);
      View worldView = new View(frame[0], frame[1], frame[4], list.camera(), w, h);
      View fixed = new View(frame[0], frame[1], frame[4], null, w, h);
      if (list.backdrop() != null) {
        this.drawSprite(list.backdrop(), worldView, targetW, targetH, false, list.tick());
        this.endSpriteRun();
      }
      for (LayerDraw layer : list.layers()) {
        if (!layer.world()) {
          continue;
        }
        this.drawCanvas(list.stageId(), layer.index(), fixed, targetW, targetH, false);
        this.drawItems(layer.items(), worldView, targetW, targetH, false, list.tick());
      }
    } else {
      this.blit(worldPicture, frame[0], frame[1], frame[2], frame[3], targetW, targetH, intoTarget);
    }

    double scale = frame[4];
    View overlay = new View(frame[0], frame[1], scale, deviceWorld ? list.camera() : this.snap(list.camera()), w, h);
    View screen = new View(frame[0], frame[1], scale, null, w, h);
    for (TextDraw text : list.overlay()) {
      this.drawText(text, overlay, targetW, targetH, intoTarget);
    }
    for (LayerDraw layer : list.layers()) {
      if (layer.world()) {
        continue;
      }
      this.drawCanvas(list.stageId(), layer.index(), screen, targetW, targetH, intoTarget);
      this.drawItems(layer.items(), screen, targetW, targetH, intoTarget, list.tick());
    }
    for (TextDraw text : list.ui()) {
      this.drawText(text, screen, targetW, targetH, intoTarget);
    }
    if (list.ask() != null) {
      this.drawAsk(list.ask(), screen, targetW, targetH, intoTarget);
    }
    for (MonitorDraw monitor : list.monitors()) {
      this.drawMonitor(monitor, screen, targetW, targetH, intoTarget);
    }
    if (!this.isCapture(out)) {
      // extensions' overlays (Studio's watch pins) stay out of captures; before the first
      // capture both are null, and the window must still get them
      this.drawItems(list.overlayItems(), screen, targetW, targetH, intoTarget, list.tick());
    }
    if (list.debug() && !this.isCapture(out)) {
      // measured, not part of the scene: captures (preview, recordings) stay reproducible
      this.drawFps(screen, targetW, targetH, intoTarget);
    }
    if (intoTarget) {
      out.resolve();
    }
  }

  // ---- sprites ----------------------------------------------------------------------------

  private void drawItems(List<Item> items, View view, int targetW, int targetH, boolean flipped, long tick) {
    for (Item item : items) {
      if (!(item instanceof SpriteDraw) && !(item instanceof ImageDraw)) {
        this.endSpriteRun();
      }
      if (item instanceof SpriteDraw s) {
        this.drawSprite(s, view, targetW, targetH, flipped, tick);
      } else if (item instanceof ShapeDraw shape) {
        this.drawShape(shape, view, targetW, targetH, flipped);
      } else if (item instanceof TextDraw text) {
        this.drawText(text, view, targetW, targetH, flipped);
      } else if (item instanceof ClipDraw clip) {
        this.clip(clip, view, targetW, targetH, flipped);
      } else if (item instanceof ImageDraw image) {
        // regions of images (tiles, glyphs) join the sprites' run
        CostumeAtlas.Region region = this.region(image.image());
        int texture = region != null ? region.texture() : this.texture(image.image()).id;
        this.spriteRun(texture, targetW, targetH, flipped);
        this.effects(this.sprite, Effects.NONE);
        double iw = image.image().getWidth(), ih = image.image().getHeight();
        double[] c = image.corners();
        double[] q = new double[8];
        for (int k = 0; k < 4; k++) {
          q[2 * k] = view.px(c[2 * k]);
          q[2 * k + 1] = view.py(c[2 * k + 1]);
        }
        float a = image.opacity();
        double u0 = image.sx() / iw, v0 = image.sy() / ih;
        double u1 = (image.sx() + image.sw()) / iw, v1 = (image.sy() + image.sh()) / ih;
        if (region != null) {
          this.batch.quad(q, region.u(u0), region.v(v0), region.u(u1), region.v(v1), a, a, a, a);
        } else {
          this.batch.quad(q, u0, v0, u1, v1, a, a, a, a);
        }
      }
    }
    this.endSpriteRun();
    glDisable(GL_SCISSOR_TEST);
  }

  // the texture of the run of sprites being drawn with the built-in program, 0 when none
  private int runTexture;

  /**
   * Consecutive sprites (and image regions) drawn with the built-in program go into one batch:
   * the program and projection are set once, and only a change of texture draws what was
   * collected. Everything else ends the run first ({@link #endSpriteRun()}).
   */
  private void spriteRun(int texture, int targetW, int targetH, boolean flipped) {
    if (this.runTexture == 0) {
      this.batch.flush();
      glUseProgram(this.sprite.id);
      this.projection(this.sprite, targetW, targetH, flipped);
      glUniform1i(this.sprite.uniform("image"), 0);
      glActiveTexture(GL_TEXTURE0);
    }
    if (this.runTexture != texture) {
      this.batch.flush();
      glBindTexture(GL_TEXTURE_2D, texture);
      this.runTexture = texture;
    }
  }

  /**
   * Where a small costume lies in the atlas, put there on first use; null for a costume too large
   * for it. Uploading binds a texture: a run draws what it collected first.
   */
  private CostumeAtlas.Region region(Image image) {
    if (!CostumeAtlas.fits(image)) {
      return null;
    }
    CostumeAtlas.Region region = this.atlas.find(image);
    if (region == null) {
      this.endSpriteRun();
      region = this.atlas.add(image, Gpu.premultiply(image.getArgb()));
    }
    return region;
  }

  private void endSpriteRun() {
    if (this.runTexture != 0) {
      this.batch.flush();
      this.runTexture = 0;
    }
  }

  /** Scissor to a rectangle of layer coordinates; targets keep their top row at GL row 0. */
  private void clip(ClipDraw clip, View view, int targetW, int targetH, boolean flipped) {
    if (!clip.active()) {
      glDisable(GL_SCISSOR_TEST);
      return;
    }
    double x0 = Math.min(view.px(clip.left()), view.px(clip.right()));
    double x1 = Math.max(view.px(clip.left()), view.px(clip.right()));
    double y0 = Math.min(view.py(clip.top()), view.py(clip.bottom()));
    double y1 = Math.max(view.py(clip.top()), view.py(clip.bottom()));
    int left = (int) Math.floor(x0), width = (int) Math.ceil(x1) - left;
    int top = (int) Math.floor(y0), height = (int) Math.ceil(y1) - top;
    glEnable(GL_SCISSOR_TEST);
    glScissor(left, flipped ? top : targetH - top - height, Math.max(0, width), Math.max(0, height));
  }

  private void drawSprite(SpriteDraw s, View view, int targetW, int targetH, boolean flipped, long tick) {
    Image image = s.costume();
    List<ShaderDraw> shaders = s.shaders();
    if (shaders.isEmpty() || this.studentProgram(shaders.get(shaders.size() - 1)) == null) {
      // the built-in program: part of a run of sprites
      CostumeAtlas.Region region = this.region(image);
      this.spriteRun(region != null ? region.texture() : this.texture(image).id, targetW, targetH, flipped);
      this.effects(this.sprite, s.effects());
      this.spriteQuads(s, view, region);
      return;
    }
    this.endSpriteRun();
    // shaders read the costume as a texture of its own (uv from 0 to 1, its resolution)
    Texture texture = this.texture(image);
    Program program = this.sprite;
    if (!shaders.isEmpty()) {
      Program student = this.studentProgram(shaders.get(shaders.size() - 1));
      if (student != null) {
        if (shaders.size() > 1) {
          texture = this.chain(shaders.subList(0, shaders.size() - 1), texture, tick, targetW, targetH, flipped);
        }
        program = student;
      }
    }
    glUseProgram(program.id);
    this.projection(program, targetW, targetH, flipped);
    glUniform1i(program.uniform(program == this.sprite ? "image" : "costume"), 0);
    if (program != this.sprite) {
      ShaderDraw last = shaders.get(shaders.size() - 1);
      glUniform2f(program.uniform("resolution"), image.getWidth(), image.getHeight());
      glUniform1f(program.uniform("time"), (float) (tick / 60.0));
      setUniforms(program, last);
    }
    this.effects(program, s.effects());
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture.id);
    this.spriteQuads(s, view, null);
    this.batch.flush();
  }

  /** The sprite's quads, with texture coordinates in its atlas region if it has one. */
  private void spriteQuads(SpriteDraw s, View view, CostumeAtlas.Region region) {
    for (double[] piece : this.spritePieces(s, view)) {
      double u0 = piece[8], v0 = piece[9], u1 = piece[10], v1 = piece[11];
      if (region != null) {
        u0 = region.u(u0);
        v0 = region.v(v0);
        u1 = region.u(u1);
        v1 = region.v(v1);
      }
      this.batch.quad(new double[] {piece[0], piece[1], piece[2], piece[3], piece[4], piece[5], piece[6], piece[7]},
          u0, v0, u1, v1, 1, 1, 1, 1);
    }
  }

  /**
   * The quads of a sprite in target pixels: {x0..y3 (top-left, top-right, bottom-right,
   * bottom-left), u0, v0, u1, v1}. One quad, or nine for a nine-sliced sprite at its own size.
   */
  private List<double[]> spritePieces(SpriteDraw s, View view) {
    int iw = s.costume().getWidth(), ih = s.costume().getHeight();
    double x = s.x(), y = s.y();
    if (this.pixelArt && !this.smoothWorld) {
      x = Math.round(x); // sprites sit on whole stage pixels; logic keeps its doubles
      y = Math.round(y);
    }
    double r = Math.toRadians(s.rotation()), cos = Math.cos(r), sin = Math.sin(r);
    boolean mirrored = s.scaleX() < 0;
    double sx = Math.abs(s.scaleX()), sy = Math.abs(s.scaleY());
    // the drawn rectangle in "drawn pixels" (costume pixels × scale), origin at its top left
    double dw = iw * sx, dh = ih * sy, ox = s.originX() * sx, oy = s.originY() * sy;
    double[] xs, ys, us, vs;
    int[] slice = s.nineSlice();
    if (slice != null) {
      double[][] h = SpriteDraw.slices(iw, slice[0], slice[2], dw, s.sliceScale());
      double[][] v = SpriteDraw.slices(ih, slice[1], slice[3], dh, s.sliceScale());
      xs = h[0];
      us = h[1];
      ys = v[0];
      vs = v[1];
    } else {
      xs = new double[] {0, dw};
      ys = new double[] {0, dh};
      us = new double[] {0, 1};
      vs = new double[] {0, 1};
    }
    List<double[]> pieces = new ArrayList<>();
    for (int j = 0; j + 1 < ys.length; j++) {
      for (int i = 0; i + 1 < xs.length; i++) {
        double[] q = new double[12];
        double[][] corners = {{xs[i], ys[j]}, {xs[i + 1], ys[j]}, {xs[i + 1], ys[j + 1]}, {xs[i], ys[j + 1]}};
        for (int k = 0; k < 4; k++) {
          double lx = corners[k][0] - ox, ly = -(corners[k][1] - oy); // y up from the origin
          if (mirrored) {
            lx = -lx;
          }
          // clockwise turn in y-up coordinates
          double wx = x + lx * cos + ly * sin, wy = y - lx * sin + ly * cos;
          q[2 * k] = view.px(wx);
          q[2 * k + 1] = view.py(wy);
        }
        q[8] = us[i];
        q[9] = vs[j];
        q[10] = us[i + 1];
        q[11] = vs[j + 1];
        pieces.add(q);
      }
    }
    return pieces;
  }

  /** The effects of the quads that follow (they go with the vertices). */
  private void effects(Program program, Effects e) {
    double turns = (e.colorEffect() / 200) % 1;
    int t = e.tint();
    this.batch.effects((float) (turns < 0 ? turns + 1 : turns), ((t >> 16) & 0xff) / 255f, ((t >> 8) & 0xff) / 255f,
        (t & 0xff) / 255f, (t >>> 24) / 255f, (float) (1 - Math.max(0, Math.min(100, e.transparency())) / 100));
  }

  /** Uploads an image now rather than when it is first drawn (the loading screen). */
  public void prepare(Image image) {
    if (this.region(image) == null) {
      this.texture(image);
    }
  }

  private Texture texture(Image image) {
    Texture t = this.textures.get(image);
    if (t == null) {
      this.endSpriteRun(); // uploading binds the new texture
      t = new Texture(image.getWidth(), image.getHeight(), Gpu.premultiply(image.getArgb()), !this.pixelArt,
          !this.pixelArt);
      this.textures.put(image, t);
    }
    return t;
  }

  // ---- canvases -------------------------------------------------------------------------

  private void uploadCanvases(DrawList list) {
    if (this.uploaded.containsKey(list)) {
      return;
    }
    if (this.uploaded.size() > 8) {
      this.uploaded.clear(); // only the last few lists can be drawn again
    }
    this.uploaded.put(list, Boolean.TRUE);
    Texture[] textures = this.canvases.computeIfAbsent(list.stageId(), id -> new Texture[list.layers().size()]);
    for (LayerDraw layer : list.layers()) {
      if (!layer.canvas()) {
        continue;
      }
      Texture t = textures[layer.index()];
      if (t == null) {
        t = new Texture(list.width(), list.height(), null, !this.pixelArt && !layer.world(), false);
        textures[layer.index()] = t;
      }
      for (CanvasRegion region : layer.canvasChanges()) {
        t.update(region.x(), region.y(), region.width(), region.height(), region.argb());
      }
    }
    this.stageLastSeen.put(list.stageId(), System.nanoTime());
  }

  private void drawCanvas(int stageId, int layer, View view, int targetW, int targetH, boolean flipped) {
    Texture[] textures = this.canvases.get(stageId);
    if (textures == null || textures[layer] == null) {
      return;
    }
    double x0 = view.px(-view.width() / 2.0), y0 = view.py(view.height() / 2.0);
    double x1 = view.px(view.width() / 2.0), y1 = view.py(-view.height() / 2.0);
    this.drawTexture(textures[layer], x0, y0, x1, y1, targetW, targetH, flipped);
  }

  /** Canvas textures of stages not shown for 30 s go (the stage resends them if shown again). */
  private void forgetOldStages(int current) {
    if (this.frames % 600 != 0) {
      return;
    }
    long now = System.nanoTime();
    this.stageLastSeen.entrySet().removeIf(e -> {
      if (e.getKey() != current && now - e.getValue() > 30_000_000_000L) {
        Texture[] old = this.canvases.remove(e.getKey());
        if (old != null) {
          for (Texture t : old) {
            if (t != null) {
              t.delete();
            }
          }
        }
        return true;
      }
      return false;
    });
  }

  // ---- shaders ----------------------------------------------------------------------------

  private Program studentProgram(ShaderDraw shader) {
    if (this.brokenShaders.contains(shader.source())) {
      return null;
    }
    Program p = this.studentPrograms.get(shader.source());
    if (p == null) {
      try {
        p = Programs.link(shader.name(), Programs.VERTEX, Programs.studentFragment(shader.source()));
      } catch (Programs.ShaderException e) {
        // shown once; the sprite is drawn without it
        System.err.println("Scratch for Java: the shader " + e.getMessage());
        this.brokenShaders.add(shader.source());
        return null;
      }
      this.studentPrograms.put(shader.source(), p);
    }
    return p;
  }

  private static void setUniforms(Program program, ShaderDraw shader) {
    for (var entry : shader.uniforms().entrySet()) {
      String name = entry.getKey();
      Object value = entry.getValue();
      if (name.endsWith("[]")) {
        float[] f = (float[]) value;
        int per = (int) f[0];
        float[] values = java.util.Arrays.copyOfRange(f, 1, f.length);
        int location = program.uniform(name.substring(0, name.length() - 2));
        switch (per) {
          case 2 -> glUniform2fv(location, values);
          case 3 -> glUniform3fv(location, values);
          case 4 -> glUniform4fv(location, values);
          default -> glUniform1fv(location, values);
        }
      } else if (value instanceof float[] f) {
        int location = program.uniform(name);
        switch (f.length) {
          case 2 -> glUniform2f(location, f[0], f[1]);
          case 3 -> glUniform3f(location, f[0], f[1], f[2]);
          case 4 -> glUniform4f(location, f[0], f[1], f[2], f[3]);
          default -> glUniform1f(location, f[0]);
        }
      } else if (value instanceof int[] i) {
        glUniform1i(program.uniform(name), i[0]);
      }
    }
  }

  /** All but the last shader of a sprite, each into a costume-sized texture of its own. */
  private Texture chain(List<ShaderDraw> shaders, Texture input, long tick, int targetW, int targetH,
      boolean flipped) {
    int previous = glGetInteger(GL_FRAMEBUFFER_BINDING);
    int[] viewport = new int[4];
    glGetIntegerv(GL_VIEWPORT, viewport);
    Texture current = input;
    for (ShaderDraw shader : shaders) {
      Program p = this.studentProgram(shader);
      if (p == null) {
        continue;
      }
      Target pass = new Target(current.width, current.height, 1, current.smooth);
      pass.bind();
      clear(0);
      glUseProgram(p.id);
      this.projection(p, pass.width, pass.height, true);
      glUniform1i(p.uniform("costume"), 0);
      glUniform2f(p.uniform("resolution"), current.width, current.height);
      glUniform1f(p.uniform("time"), (float) (tick / 60.0));
      setUniforms(p, shader);
      this.effects(p, Effects.NONE);
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, current.id);
      this.batch.quad(new double[] {0, 0, pass.width, 0, pass.width, pass.height, 0, pass.height}, 0, 0, 1, 1,
          1, 1, 1, 1);
      this.batch.flush();
      pass.resolve();
      current = pass.texture; // the targets of a chain are short-lived: per frame
      this.toDelete.add(pass);
    }
    glBindFramebuffer(GL_FRAMEBUFFER, previous);
    glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    return current;
  }

  private final List<Target> toDelete = new ArrayList<>();

  /** The stage's shaders over the world picture, alternating between two targets. */
  private Texture applyStageShaders(DrawList list, Target world) {
    for (Target t : this.toDelete) {
      t.delete();
    }
    this.toDelete.clear();
    Texture current = world.texture;
    for (ShaderDraw shader : list.stageShaders()) {
      Program p = this.studentProgram(shader);
      if (p == null) {
        continue;
      }
      this.worldPing = this.sized(this.worldPing, world.width, world.height, 1);
      this.worldPong = this.sized(this.worldPong, world.width, world.height, 1);
      Target out = current == this.worldPing.texture ? this.worldPong : this.worldPing;
      out.bind();
      clear(0);
      glUseProgram(p.id);
      this.projection(p, out.width, out.height, true);
      glUniform1i(p.uniform("costume"), 0);
      glUniform2f(p.uniform("resolution"), out.width, out.height);
      glUniform1f(p.uniform("time"), (float) (list.tick() / 60.0));
      setUniforms(p, shader);
      this.effects(p, Effects.NONE);
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, current.id);
      this.batch.quad(new double[] {0, 0, out.width, 0, out.width, out.height, 0, out.height}, 0, 0, 1, 1, 1, 1, 1,
          1);
      this.batch.flush();
      out.resolve();
      current = out.texture;
    }
    return current;
  }

  private void drawTransition(TransitionDraw t, Target from, Target to, int targetW, int targetH, double[] frame,
      boolean flipped) {
    String source = t.source() != null ? t.source() : Programs.BUILT_IN_TRANSITIONS.get(t.name());
    if (source == null) {
      source = Programs.BUILT_IN_TRANSITIONS.get("fade");
    }
    Program p = this.transitionPrograms.get(source);
    if (p == null) {
      try {
        p = Programs.link(t.name(), Programs.VERTEX, Programs.transitionFragment(source));
      } catch (Programs.ShaderException e) {
        System.err.println("Scratch for Java: the transition " + e.getMessage());
        p = Programs.link("fade", Programs.VERTEX,
            Programs.transitionFragment(Programs.BUILT_IN_TRANSITIONS.get("fade")));
      }
      this.transitionPrograms.put(source, p);
    }
    glUseProgram(p.id);
    this.projection(p, targetW, targetH, flipped);
    glUniform1i(p.uniform("from"), 0);
    glUniform1i(p.uniform("to"), 1);
    glUniform1f(p.uniform("progress"), (float) Math.max(0, Math.min(1, t.progress())));
    glUniform2f(p.uniform("resolution"), (float) frame[2], (float) frame[3]);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, to.texture.id);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, from.texture.id);
    this.batch.quad(new double[] {frame[0], frame[1], frame[0] + frame[2], frame[1], frame[0] + frame[2],
        frame[1] + frame[3], frame[0], frame[1] + frame[3]}, 0, 0, 1, 1, 1, 1, 1, 1);
    this.batch.flush();
  }

  // ---- texts, boxes, monitors ----------------------------------------------------------------

  private void drawText(TextDraw t, View view, int targetW, int targetH, boolean flipped) {
    double unit = view.unit();
    float[] box = premultiplied(t.boxColor()), stroke = premultiplied(t.boxStroke());
    double left = view.px(t.x()), top = view.py(t.y());
    double w = t.width() * unit, h = t.height() * unit;
    // a box has the corners of Scratch's ask box, bubbles the round ones of Scratch's bubbles
    double radius = (t.box() == TextDraw.Box.PLAIN ? 8 : 16) * unit, border = 2 * unit;
    switch (t.box()) {
      case PLAIN -> {
        this.roundedRect(left - border / 2, top - border / 2, w + border, h + border, radius, stroke);
        this.roundedRect(left + border / 2, top + border / 2, w - border, h - border, radius, box);
      }
      case SPEECH -> {
        // the tail leaves the bubble's corner next to the sprite: left, or right when mirrored
        double tail = view.px(t.tailX());
        double dir = t.tailX() > t.x() + t.width() / 2 ? -1 : 1;
        this.roundedRect(left - border / 2, top - border / 2, w + border, h + border, radius, stroke);
        this.triangle(tail + dir * (10 * unit - border), top + h - border, tail + dir * (10 * unit - border),
            top + h + 20 * unit + border, tail + dir * (30 * unit + border), top + h - border, stroke);
        this.roundedRect(left + border / 2, top + border / 2, w - border, h - border, radius, box);
        this.triangle(tail + dir * 10 * unit, top + h - 2 * border, tail + dir * 10 * unit,
            top + h + 20 * unit - border, tail + dir * 26 * unit, top + h - 2 * border, box);
      }
      case THOUGHT -> {
        double tail = view.px(t.tailX());
        double dir = t.tailX() > t.x() + t.width() / 2 ? -1 : 1;
        this.roundedRect(left - border / 2, top - border / 2, w + border, h + border, radius, stroke);
        this.roundedRect(left + border / 2, top + border / 2, w - border, h - border, radius, box);
        double[][] dots = {{20, 0, 5}, {7, 7, 3}, {0, 10, 2}};
        for (double[] d : dots) {
          this.circle(tail + dir * d[0] * unit, top + h + d[1] * unit, (d[2] + 1) * unit, stroke);
          this.circle(tail + dir * d[0] * unit, top + h + d[1] * unit, d[2] * unit, box);
        }
      }
      default -> {
      }
    }
    this.flushShapes(targetW, targetH, flipped);
    this.drawLayout(t.layout(), t, view, targetW, targetH, flipped);
  }

  /** The letters of a layout inside a text's box, aligned and at the device size. */
  private void drawLayout(TextLayout layout, TextDraw t, View view, int targetW, int targetH, boolean flipped) {
    double unit = view.unit();
    double deviceSize = layout.size() * unit;
    double inner = t.width() - 2 * t.padding();
    float[] color = premultiplied(t.color());
    glUseProgram(this.coverage.id);
    this.projection(this.coverage, targetW, targetH, flipped);
    glUniform1i(this.coverage.uniform("image"), 0);
    glUniform1i(this.coverage.uniform("useTexture"), 1);
    glActiveTexture(GL_TEXTURE0);
    int page = -1;
    for (TextLayout.Line line : layout.lines()) {
      double offset = switch (t.align()) {
        case 1 -> (inner - line.width()) / 2;
        case 2 -> inner - line.width();
        default -> 0;
      };
      double baseline = view.py(t.y() - t.padding() - line.baseline());
      baseline = Math.round(baseline);
      for (TextLayout.Glyph g : line.glyphs()) {
        double penX = view.px(t.x() + t.padding() + offset + g.x());
        if (this.pixelArt) {
          penX = Math.round(penX);
        }
        GlyphAtlas.Slot slot = this.glyphs.glyph(g.font(), g.codepoint(), deviceSize, penX - Math.floor(penX),
            this.pixelArt);
        if (slot.page() < 0 || slot.width() == 0) {
          continue;
        }
        if (slot.page() != page) {
          this.batch.flush();
          page = slot.page();
          glBindTexture(GL_TEXTURE_2D, this.glyphs.page(page));
        }
        double x0 = Math.floor(penX) + slot.offsetX(), y0 = baseline + slot.offsetY();
        double p = GlyphAtlas.PAGE;
        this.batch.quad(new double[] {x0, y0, x0 + slot.width(), y0, x0 + slot.width(), y0 + slot.height(), x0,
            y0 + slot.height()}, slot.x() / p, slot.y() / p, (slot.x() + slot.width()) / p,
            (slot.y() + slot.height()) / p, color[0], color[1], color[2], color[3]);
      }
    }
    this.batch.flush();
  }

  private void drawMonitor(MonitorDraw m, View view, int targetW, int targetH, boolean flipped) {
    double unit = view.unit();
    double labelW = m.label().width(), valueW = Math.max(30, m.value().width() + 14);
    double boxW = labelW + valueW + 18;
    double left = view.px(m.x()), top = view.py(m.y());
    this.roundedRect(left - unit, top - unit, (boxW + 2) * unit, (MonitorDraw.HEIGHT + 2) * unit, 5 * unit,
        premultiplied(0xffc3ccd9));
    this.roundedRect(left, top, boxW * unit, MonitorDraw.HEIGHT * unit, 4 * unit, premultiplied(0xebe6f0ff));
    this.roundedRect(left + (labelW + 12) * unit, top + 3 * unit, valueW * unit, 18 * unit, 9 * unit,
        premultiplied(0xffff8c1a));
    this.flushShapes(targetW, targetH, flipped);
    double ascent = m.label().font().ascent(m.label().size()), lineH = m.label().height();
    // label left in the box, value centred in the pill; both vertically centred
    TextDraw label = new TextDraw(m.label(), m.x() + 6, m.y() - (MonitorDraw.HEIGHT - lineH) / 2, labelW, lineH, 0,
        0xff575e75, TextDraw.Box.NONE, 0, 0, 0, 0);
    TextDraw value = new TextDraw(m.value(), m.x() + labelW + 12, m.y() - (MonitorDraw.HEIGHT - lineH) / 2, valueW,
        lineH, 1, 0xffffffff, TextDraw.Box.NONE, 0, 0, 0, 0);
    this.drawLayout(m.label(), label, view, targetW, targetH, flipped);
    this.drawLayout(m.value(), value, view, targetW, targetH, flipped);
  }

  /** Scratch's ask box: a white card, the question, the answer field with its check button. */
  private void drawAsk(AskDraw a, View view, int targetW, int targetH, boolean flipped) {
    double unit = view.unit();
    float[] white = premultiplied(0xffffffff), border = premultiplied(0x26000000), blue = premultiplied(0xff4c97ff);
    int textColor = 0xff575e75;
    double left = view.px(a.x()), top = view.py(a.y()), w = a.width() * unit, h = a.height() * unit;
    this.roundedRect(left, top, w, h, 8 * unit, border);
    this.roundedRect(left + 2 * unit, top + 2 * unit, w - 4 * unit, h - 4 * unit, 6 * unit, white);
    double fx = view.px(a.fieldX()), fy = view.py(a.fieldY());
    double fw = a.fieldWidth() * unit, fh = AskDraw.FIELD * unit;
    this.roundedRect(fx, fy, fw, fh, fh / 2, border);
    this.roundedRect(fx + unit, fy + unit, fw - 2 * unit, fh - 2 * unit, fh / 2 - unit, white);
    double bx = view.px(a.buttonX()), by = view.py(a.buttonY());
    this.circle(bx, by, AskDraw.BUTTON / 2 * unit, blue);
    float[] tick = premultiplied(0xffffffff);
    double s = unit, stroke = 2.5 * unit;
    this.line(bx - 5 * s, by, bx - 1.5 * s, by + 3.5 * s, stroke, tick);
    this.line(bx - 1.5 * s, by + 3.5 * s, bx + 5 * s, by - 3.5 * s, stroke, tick);
    this.circle(bx - 1.5 * s, by + 3.5 * s, stroke / 2, tick);
    TextLayout answer = a.answer();
    double lineH = answer.font().lineHeight(answer.size());
    double answerY = a.fieldY() - (AskDraw.FIELD - lineH) / 2;
    if (a.caret()) {
      double cx = view.px(a.fieldX() + AskDraw.INSET + answer.width()) + unit;
      this.roundedRect(Math.round(cx), view.py(answerY), Math.max(1, Math.round(unit)), lineH * unit, 0,
          premultiplied(textColor));
    }
    this.flushShapes(targetW, targetH, flipped);
    if (a.question() != null) {
      TextLayout q = a.question();
      TextDraw label = new TextDraw(q, a.x() + AskDraw.PADDING, a.y() - AskDraw.PADDING, q.width(), q.height(), 0,
          textColor, TextDraw.Box.NONE, 0, 0, 0, 0);
      this.drawLayout(q, label, view, targetW, targetH, flipped);
    }
    TextDraw typed = new TextDraw(answer, a.fieldX() + AskDraw.INSET, answerY, answer.width(), lineH, 0, textColor,
        TextDraw.Box.NONE, 0, 0, 0, 0);
    this.drawLayout(answer, typed, view, targetW, targetH, flipped);
  }

  private void drawFps(View view, int targetW, int targetH, boolean flipped) {
    TextLayout layout = TextLayout.of(String.format(java.util.Locale.ROOT, "%.0f fps", this.fps), Font.defaultFont(),
        12, 0);
    TextDraw text = new TextDraw(layout, view.width() / 2.0 - layout.width() - 6, view.height() / 2.0 - 4,
        layout.width(), layout.height(), 0, 0xffff0000, TextDraw.Box.NONE, 0, 0, 0, 0);
    this.drawLayout(layout, text, view, targetW, targetH, flipped);
  }

  // ---- shapes, tessellated on the CPU ---------------------------------------------------------

  private void drawShape(ShapeDraw shape, View view, int targetW, int targetH, boolean flipped) {
    double[] p = shape.points();
    int n = p.length / 2;
    if (shape.fill() != 0 && n >= 3) {
      float[] c = premultiplied(shape.fill());
      for (double[] piece : org.openpatch.scratch.internal.geom.Geometry.convexPieces(p)) {
        // a fan over each convex piece
        for (int i = 1; i + 1 < piece.length / 2; i++) {
          this.triangle(view.px(piece[0]), view.py(piece[1]), view.px(piece[2 * i]), view.py(piece[2 * i + 1]),
              view.px(piece[2 * i + 2]), view.py(piece[2 * i + 3]), c);
        }
      }
    }
    if (shape.stroke() != 0 && n >= 2) {
      float[] c = premultiplied(shape.stroke());
      double half = shape.strokeWeight() * view.unit() / 2;
      for (int i = 0; i < n; i++) {
        int j = (i + 1) % n;
        double x0 = view.px(p[2 * i]), y0 = view.py(p[2 * i + 1]), x1 = view.px(p[2 * j]), y1 = view.py(p[2 * j + 1]);
        double dx = x1 - x0, dy = y1 - y0, len = Math.hypot(dx, dy);
        if (len == 0) {
          continue;
        }
        double nx = -dy / len * half, ny = dx / len * half;
        this.batch.quad(new double[] {x0 + nx, y0 + ny, x1 + nx, y1 + ny, x1 - nx, y1 - ny, x0 - nx, y0 - ny}, 0, 0,
            1, 1, c[0], c[1], c[2], c[3]);
      }
    }
    this.flushShapes(targetW, targetH, flipped);
  }

  private void roundedRect(double x, double y, double w, double h, double radius, float[] c) {
    if (w <= 0 || h <= 0) {
      return;
    }
    double r = Math.min(radius, Math.min(w, h) / 2);
    // the middle cross, then a fan at each corner
    this.batch.quad(new double[] {x + r, y, x + w - r, y, x + w - r, y + h, x + r, y + h}, 0, 0, 1, 1, c[0], c[1],
        c[2], c[3]);
    this.batch.quad(new double[] {x, y + r, x + r, y + r, x + r, y + h - r, x, y + h - r}, 0, 0, 1, 1, c[0], c[1],
        c[2], c[3]);
    this.batch.quad(new double[] {x + w - r, y + r, x + w, y + r, x + w, y + h - r, x + w - r, y + h - r}, 0, 0, 1, 1,
        c[0], c[1], c[2], c[3]);
    double[][] centres = {{x + r, y + r, Math.PI, 1.5 * Math.PI}, {x + w - r, y + r, 1.5 * Math.PI, 2 * Math.PI},
        {x + w - r, y + h - r, 0, 0.5 * Math.PI}, {x + r, y + h - r, 0.5 * Math.PI, Math.PI}};
    int segments = 8;
    for (double[] k : centres) {
      for (int i = 0; i < segments; i++) {
        double a0 = k[2] + (k[3] - k[2]) * i / segments, a1 = k[2] + (k[3] - k[2]) * (i + 1) / segments;
        this.batch.vertex(k[0], k[1], 0, 0, c[0], c[1], c[2], c[3]);
        this.batch.vertex(k[0] + r * Math.cos(a0), k[1] + r * Math.sin(a0), 0, 0, c[0], c[1], c[2], c[3]);
        this.batch.vertex(k[0] + r * Math.cos(a1), k[1] + r * Math.sin(a1), 0, 0, c[0], c[1], c[2], c[3]);
      }
    }
  }

  private void triangle(double x0, double y0, double x1, double y1, double x2, double y2, float[] c) {
    this.batch.vertex(x0, y0, 0, 0, c[0], c[1], c[2], c[3]);
    this.batch.vertex(x1, y1, 0, 0, c[0], c[1], c[2], c[3]);
    this.batch.vertex(x2, y2, 0, 0, c[0], c[1], c[2], c[3]);
  }

  /** A straight stroke of the given width from one point to another. */
  private void line(double x0, double y0, double x1, double y1, double width, float[] c) {
    double len = Math.hypot(x1 - x0, y1 - y0);
    if (len == 0) {
      return;
    }
    double nx = -(y1 - y0) / len * width / 2, ny = (x1 - x0) / len * width / 2;
    this.triangle(x0 + nx, y0 + ny, x1 + nx, y1 + ny, x1 - nx, y1 - ny, c);
    this.triangle(x0 + nx, y0 + ny, x1 - nx, y1 - ny, x0 - nx, y0 - ny, c);
  }

  private void circle(double cx, double cy, double r, float[] c) {
    int segments = 16;
    for (int i = 0; i < segments; i++) {
      double a0 = 2 * Math.PI * i / segments, a1 = 2 * Math.PI * (i + 1) / segments;
      this.triangle(cx, cy, cx + r * Math.cos(a0), cy + r * Math.sin(a0), cx + r * Math.cos(a1), cy + r * Math.sin(a1),
          c);
    }
  }

  /** Draws the shapes collected so far with the solid-colour program. */
  private void flushShapes(int targetW, int targetH, boolean flipped) {
    if (this.batch.isEmpty()) {
      return;
    }
    glUseProgram(this.coverage.id);
    this.projection(this.coverage, targetW, targetH, flipped);
    glUniform1i(this.coverage.uniform("useTexture"), 0);
    this.batch.flush();
  }

  // ---- helpers ----------------------------------------------------------------------------

  /** Target pixels (origin top left, y down) to clip space; targets keep their top row at row 0. */
  private void projection(Program program, int w, int h, boolean flipped) {
    float sy = flipped ? 2f / h : -2f / h, ty = flipped ? -1f : 1f;
    float[] m = {2f / w, 0, 0, 0, sy, 0, -1f, ty, 1};
    glUniformMatrix3fv(program.uniform("transform"), false, m);
  }

  private void blit(Texture texture, double x, double y, double w, double h, int targetW, int targetH, boolean flipped) {
    this.drawTexture(texture, x, y, x + w, y + h, targetW, targetH, flipped);
  }

  /**
   * The world picture mixed with the previous frame: {@code out = (1-a)·current + a·previous}.
   * Two samples per presented frame halve the time a 60 Hz screen holds one picture, which the
   * eye sees as less smear while things move; the moment the picture is still again, current and
   * previous are equal, so it is fully sharp at once. The raw previous frame is kept (not the
   * blended one), so there are no ghost trails: exactly two frames take part.
   *
   * @param picture the world as it is this frame, at stage size
   * @param w the stage width
   * @param h the stage height
   * @return the blended picture; the overlay and UI are drawn on top of it as usual
   */
  private Texture smooth(Texture picture, int w, int h) {
    this.smoothPicture = this.sized(this.smoothPicture, w, h, 1);
    this.smoothPrevious = this.sized(this.smoothPrevious, w, h, 1);
    this.smoothPicture.bind();
    this.drawTexture(picture, 0, 0, w, h, w, h, true);
    if (this.smoothHasPrevious) {
      this.drawTexture(this.smoothPrevious.texture, 0, 0, w, h, w, h, true,
          new Effects(0, 100 - this.smoothing * 100, 0xffffffff));
    }
    this.smoothPrevious.bind();
    this.drawTexture(picture, 0, 0, w, h, w, h, true);
    this.smoothHasPrevious = true;
    return this.smoothPicture.texture;
  }

  /** A texture into a rectangle of the target, without effects. */
  private void drawTexture(Texture texture, double x0, double y0, double x1, double y1, int targetW, int targetH,
      boolean flipped) {
    this.drawTexture(texture, x0, y0, x1, y1, targetW, targetH, flipped, Effects.NONE);
  }

  /** A texture into a rectangle of the target, with these effects. */
  private void drawTexture(Texture texture, double x0, double y0, double x1, double y1, int targetW, int targetH,
      boolean flipped, Effects e) {
    glUseProgram(this.sprite.id);
    this.projection(this.sprite, targetW, targetH, flipped);
    glUniform1i(this.sprite.uniform("image"), 0);
    this.effects(this.sprite, e);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture.id);
    this.batch.quad(new double[] {x0, y0, x1, y0, x1, y1, x0, y1}, 0, 0, 1, 1, 1, 1, 1, 1);
    this.batch.flush();
  }

  private Target sized(Target t, int w, int h, int samples) {
    if (t != null && t.width == w && t.height == h) {
      return t;
    }
    if (t != null) {
      t.delete();
    }
    return new Target(Math.max(1, w), Math.max(1, h), samples, !this.pixelArt);
  }

  private static void clear(int argb) {
    glClearColor(((argb >> 16) & 0xff) / 255f, ((argb >> 8) & 0xff) / 255f, (argb & 0xff) / 255f,
        (argb >>> 24) / 255f);
    glClear(GL_COLOR_BUFFER_BIT);
  }

  private static float[] premultiplied(int argb) {
    float a = (argb >>> 24) / 255f;
    return new float[] {((argb >> 16) & 0xff) / 255f * a, ((argb >> 8) & 0xff) / 255f * a, (argb & 0xff) / 255f * a,
        a};
  }

  /** Frees every GPU object; on the window thread, before the context goes. */
  public void delete() {
    for (Texture t : this.textures.values()) {
      t.delete();
    }
    for (Texture[] ts : this.canvases.values()) {
      for (Texture t : ts) {
        if (t != null) {
          t.delete();
        }
      }
    }
    this.glyphs.delete();
    this.white.delete();
    this.atlas.delete();
  }
}
