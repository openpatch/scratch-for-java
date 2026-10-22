package org.openpatch.scratch.internal.window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Launcher;
import org.openpatch.scratch.MouseCode;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.Preview;
import org.openpatch.scratch.internal.Release;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.input.InputEvent;
import org.openpatch.scratch.internal.loop.GameLoop;
import org.openpatch.scratch.internal.loop.TickClock;
import org.openpatch.scratch.internal.render.Renderer;

/**
 * The window: GLFW and OpenGL on the window thread, the game loop in lockstep with the game thread,
 * input turned into events for the game. The window thread is the process's
 * first thread under {@link Launcher} (macOS: {@code -XstartOnFirstThread}); in a plain
 * {@code java} run it is a thread of the library's, with {@code glfw_async} on macOS.
 */
public final class WindowHost {

  /** What the window needs to know before it opens. */
  public record Settings(String title, boolean fullScreen, boolean pixelArt, boolean smoothPixelArt,
      double frameSmoothing, String splashLogo, String assets, List<Extension> extensions) {}

  private final Game game;
  private final Window window;
  private final Settings settings;
  private final CountDownLatch opened = new CountDownLatch(1);
  private final TickClock clock = new TickClock();
  private volatile long handle;
  private volatile boolean closeRequested;
  private GameLoop loop;
  private Renderer renderer;
  private DrawList shown;
  /** Loading screen at startup (null once it is gone) and the assets it waits for. */
  private LoadingScreen loadingScreen;
  private org.openpatch.scratch.internal.Assets assets;
  private DrawList firstStageList;
  /** Loading screen while a transition's stage is being made, or null. */
  private LoadingScreen stageScreen;
  private static final long UPLOAD_NANOS = 4_000_000;
  private static final long MAKING_STAGE_NANOS = 200_000_000;
  private double cursorX, cursorY;
  private boolean fullScreen;
  private int[] windowedBounds;
  private boolean vsync;
  /** False once vsync turned out not to wait, or with {@code -Dscratch4j.vsync=false}. */
  private boolean vsyncWorks;
  /** On Wayland vsync only while focused: a hidden window may block the swap there. */
  private boolean wayland;
  private Image cursorImage;
  private long cursor;

  private WindowHost(Game game, Window window, Settings settings) {
    this.game = game;
    this.window = window;
    this.settings = settings;
    this.fullScreen = settings.fullScreen();
  }

  /** Opens the window on its thread and returns at once, as 6.x's {@code new Window} did. */
  public static WindowHost start(Game game, Window window, Settings settings) {
    WindowHost host = new WindowHost(game, window, settings);
    game.onExit(window::whenExits);
    if (Launcher.isActive()) {
      Launcher.runOnFirstThread(host::run);
    } else {
      if (Platform.get() == Platform.MACOSX) {
        // GLFW calls are dispatched to the first thread's run loop (LWJGL's glfw_async)
        Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
      }
      Thread thread = new Thread(host::run, "scratch4j-window");
      thread.start(); // not a daemon: it keeps the program alive after main returns
    }
    return host;
  }

  /** Ends the program as closing the window does: {@code whenExits()} first. Any thread. */
  public void close() {
    this.closeRequested = true;
    long h = this.handle;
    if (h != 0) {
      glfwSetWindowShouldClose(h, true);
    }
  }

  // ---- the window thread --------------------------------------------------------------------

  private void run() {
    try {
      this.open();
    } catch (Throwable t) {
      System.err.println("Scratch for Java could not open its window: " + t.getMessage());
      if (String.valueOf(t.getMessage()).contains("Failed to locate library")) {
        // not the graphics card: LWJGL's native libraries are missing for this platform
        System.err.println("LWJGL's native libraries for " + System.getProperty("os.name") + " "
            + System.getProperty("os.arch") + " are missing. Use the scratch-<version>-all.jar, or put the "
            + "lwjgl-*-natives-<platform>.jar files next to the others (on the module path also add them with "
            + "--add-modules org.lwjgl.natives,org.lwjgl.glfw.natives,…).");
        Release.crashed("The game could not start: files it needs for this computer are missing.");
      } else {
        System.err.println("It needs OpenGL 3.3 (4.1 on macOS). Update the graphics driver, or run on another "
            + "computer.");
        Release.crashed("The game could not open its window: it needs OpenGL 3.3 (4.1 on macOS). Updating the "
            + "graphics driver may help.");
      }
      this.opened.countDown();
      System.exit(1);
      return;
    }
    this.opened.countDown();
    this.loop = new GameLoop(this.game);
    this.loop.start();
    try {
      this.frames();
    } catch (Throwable t) {
      t.printStackTrace();
      this.crashed("The game crashed: " + t);
    }
    boolean answered = this.loop.exit();
    if (!answered) {
      System.err.println("Scratch for Java: the game did not finish whenExits() within a second; quitting anyway.");
    }
    if (this.renderer != null) {
      this.renderer.delete();
    }
    glfwDestroyWindow(this.handle);
    glfwTerminate();
    System.exit(0);
  }

  private void open() {
    GLFWErrorCallback.createPrint(System.err).set();
    String platform = System.getProperty("scratch4j.platform");
    if ("x11".equals(platform)) {
      glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);
    } else if ("wayland".equals(platform)) {
      glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND);
    }
    if (!glfwInit()) {
      throw new IllegalStateException("GLFW could not start (no display?)");
    }
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
    // fullscreen is meant borderless: Alt+Tab or a click on another monitor must not minimise it
    glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE);
    boolean hidden = System.getProperty("scratch4j.preview") != null || Boolean.getBoolean("scratch4j.hidden");
    glfwWindowHint(GLFW_VISIBLE, hidden ? GLFW_FALSE : GLFW_TRUE);
    int[] size = this.initialSize();
    this.handle = glfwCreateWindow(size[0], size[1], this.settings.title(), 0, 0);
    if (this.handle == 0) {
      throw new IllegalStateException("no OpenGL 3.3 core context");
    }
    glfwMakeContextCurrent(this.handle);
    GL.createCapabilities();
    // Wayland may block glfwSwapBuffers for hidden windows: there vsync only
    // while the window has the focus (a hidden window never has it), own pacing otherwise
    this.wayland = glfwGetPlatform() == GLFW_PLATFORM_WAYLAND;
    this.vsyncWorks = !"false".equals(System.getProperty("scratch4j.vsync"));
    this.vsync = this.wantsVsync();
    glfwSwapInterval(this.vsync ? 1 : 0);
    GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    if (mode != null) {
      this.clock.useRefreshRate(mode.refreshRate());
    }
    this.renderer = new Renderer(this.settings.pixelArt(), this.settings.smoothPixelArt(),
        this.settings.frameSmoothing());
    this.installInput();
    this.assets = org.openpatch.scratch.internal.Assets.start(this.settings.assets());
    this.loadingScreen = new LoadingScreen(this.settings.splashLogo(), this.game.getWidth(), this.game.getHeight());
    this.game.getExtensions().setLoading(true);
    Preview.startTimeout();
    if (this.fullScreen) {
      this.fullScreen = false;
      this.toggleFullScreen();
    }
    if (Boolean.getBoolean("scratch4j.debugGl")) {
      System.out.printf("Scratch for Java: GL %s, %s%n", glGetString(GL_VERSION), glGetString(GL_RENDERER));
      try (MemoryStack stack = MemoryStack.stackPush()) {
        var w = stack.mallocInt(1);
        var h = stack.mallocInt(1);
        var cx = stack.mallocFloat(1);
        var cy = stack.mallocFloat(1);
        glfwGetWindowSize(this.handle, w, h);
        System.out.printf("Scratch for Java: window %dx%d", w.get(0), h.get(0));
        glfwGetFramebufferSize(this.handle, w, h);
        System.out.printf(", framebuffer %dx%d", w.get(0), h.get(0));
        glfwGetMonitorContentScale(glfwGetPrimaryMonitor(), cx, cy);
        System.out.printf(", monitor content scale %.2f", cx.get(0));
        var videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode != null) {
          System.out.printf(", mode %dx%d%n", videoMode.width(), videoMode.height());
        } else {
          System.out.printf("%n");
        }
      }
    }
  }

  /** The stage size, or in pixel art the largest whole multiple that fits the screen. */
  private int[] initialSize() {
    int w = this.game.getWidth(), h = this.game.getHeight();
    if (this.settings.pixelArt()) {
      GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      if (mode != null) {
        int factor = Math.max(1, Math.min((mode.width() - 80) / w, (mode.height() - 120) / h));
        return new int[] {w * factor, h * factor};
      }
    }
    return new int[] {w, h};
  }

  private void frames() {
    final long period = 1_000_000_000L / 60;
    long last = System.nanoTime(), next = last, second = last;
    int frames = 0;
    int unthrottled = 0;
    while (!glfwWindowShouldClose(this.handle) && !this.closeRequested) {
      glfwPollEvents();
      long now = System.nanoTime();
      DrawList list;
      if (this.loadingScreen != null) {
        list = this.loadingFrame(now);
        last = now; // no ticks owed for the time spent loading
      } else {
        this.clock.setPaused(this.game.isPaused());
        this.clock.setGameSpeed(this.game.getGameSpeed());
        for (int steps = this.game.takeSteps(); steps > 0; steps--) {
          this.clock.step();
        }
        int ticks = this.clock.advance((now - last) / 1e9);
        last = now;
        // extensions poll their devices; the game thread waits, so the hand-over needs no lock
        this.game.getExtensions().poll();
        this.game.getExtensions().handOver();
        list = this.loop.runBatch(ticks, this::pollWhileWaiting);
        this.stageScreen = null;
      }
      if (list == null) {
        break; // closed while learner code was busy
      }
      if (Release.isOn() && this.game.getStopped() != null) {
        // a released game has no console to read the error in: say so, then end
        String first = this.game.getStopped().message().strip().lines().findFirst().orElse("");
        this.crashed("The game stopped because of an error.\n" + first);
        break;
      }
      this.shown = list;
      this.updateCursor(list.cursor());
      if (this.wantsVsync() != this.vsync) {
        this.vsync = !this.vsync;
        glfwSwapInterval(this.vsync ? 1 : 0);
        next = System.nanoTime();
        unthrottled = 0;
        if (Boolean.getBoolean("scratch4j.debugGl")) {
          System.out.println("Scratch for Java: vsync " + (this.vsync ? "on" : "off (own pacing)"));
        }
      }
      this.draw();
      glfwSwapBuffers(this.handle);
      frames++;
      if (this.vsync && System.nanoTime() - last < period / 2) {
        // vsync that does not wait (Xvfb, some drivers): pace ourselves instead
        if (++unthrottled > 30) {
          this.vsyncWorks = false;
          this.vsync = false;
          glfwSwapInterval(0);
          next = System.nanoTime();
        }
      } else {
        unthrottled = 0;
      }
      now = System.nanoTime();
      if (now - second >= 1_000_000_000L) {
        this.renderer.setFps(frames * 1e9 / (now - second));
        frames = 0;
        second = now;
      }
      if (!this.vsync) {
        // own pacing at 60 frames a second where vsync could block
        next += period;
        long wait = next - System.nanoTime();
        if (wait < -5 * period) {
          next = System.nanoTime();
        } else {
          while (System.nanoTime() < next - 1_500_000) {
            sleep(1);
          }
          while (System.nanoTime() < next) {
            Thread.onSpinWait();
          }
        }
      }
    }
  }

  /**
   * Vsync where it works; on Wayland only while the window has the focus. Own pacing at 60 Hz is
   * not locked to the screen's refresh, so it shows a frame twice or skips one now and then — a
   * visible hitch in anything that scrolls.
   */
  private boolean wantsVsync() {
    return this.vsyncWorks && (!this.wayland || glfwGetWindowAttrib(this.handle, GLFW_FOCUSED) == GLFW_TRUE);
  }

  /** The crash box of a released game, with the window out of its way (fullscreen would hide it). */
  private void crashed(String what) {
    if (Release.isOn() && this.handle != 0) {
      glfwHideWindow(this.handle);
      glfwPollEvents();
    }
    Release.crashed(what);
  }

  private void pollWhileWaiting() {
    glfwPollEvents();
    if (glfwWindowShouldClose(this.handle)) {
      this.loop.close();
      return;
    }
    // a transition's stage taking long to make: the loading screen instead of a frozen picture
    long since = this.game.makingStageSince();
    long now = System.nanoTime();
    if (since != 0 && now - since > MAKING_STAGE_NANOS) {
      if (this.stageScreen == null) {
        this.stageScreen = new LoadingScreen(this.settings.splashLogo(), this.game.getWidth(), this.game.getHeight());
      }
      this.stageScreen.showing(now, false);
      this.shown = this.stageScreen.alone(now, this.game.getWidth(), this.game.getHeight(), null);
      this.draw();
      glfwSwapBuffers(this.handle);
    }
  }

  /**
   * A frame of the loading screen: uploads decoded images for a few milliseconds, then the screen
   * alone, or fading out over the stage's first picture once everything is loaded and a stage is
   * set. Input that arrives meanwhile is dropped; no tick runs.
   */
  private DrawList loadingFrame(long now) {
    long until = now + UPLOAD_NANOS;
    for (Image image; System.nanoTime() < until && (image = this.assets.nextUpload()) != null; ) {
      this.renderer.prepare(image);
    }
    this.game.dropInput();
    boolean ready = this.assets.isDone() && this.game.getStage() != null;
    boolean showing = !Preview.isPreview() && this.loadingScreen.showing(now, ready);
    if (!showing && (ready || !Preview.isPreview())) {
      this.loadingScreen = null;
      this.game.getExtensions().setLoading(false);
      return this.loop.runBatch(0, this::pollWhileWaiting);
    }
    if (this.loadingScreen.fadingOut()) {
      if (this.firstStageList == null) {
        this.firstStageList = this.loop.runBatch(0, this::pollWhileWaiting);
        if (this.firstStageList == null) {
          return null;
        }
      }
      return this.loadingScreen.over(this.firstStageList, now, this.assets);
    }
    return this.loadingScreen.alone(now, this.game.getWidth(), this.game.getHeight(), this.assets);
  }

  private void draw() {
    if (this.shown == null) {
      glClearColor(0, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT);
      return;
    }
    try (MemoryStack stack = MemoryStack.stackPush()) {
      var w = stack.mallocInt(1);
      var h = stack.mallocInt(1);
      glfwGetFramebufferSize(this.handle, w, h);
      this.renderer.render(this.shown, w.get(0), h.get(0));
    }
    if (this.game.getExtensions().wantsFrames()) {
      DrawList list = this.shown;
      this.game.getExtensions().afterFrame(this.game.getStage(), new org.openpatch.scratch.extension.ExtensionContext.Frame() {
        private int[] pixels;

        @Override
        public int getWidth() {
          return list.width();
        }

        @Override
        public int getHeight() {
          return list.height();
        }

        @Override
        public int[] getArgb() {
          if (this.pixels == null) {
            this.pixels = WindowHost.this.renderer.capture(list, true); // read back only when asked for
          }
          return this.pixels.clone();
        }

        @Override
        public void savePng(java.nio.file.Path file) throws java.io.IOException {
          new Image(list.width(), list.height(), this.getArgb()).writePng(file);
        }
      });
    }
    // Studio's preview and capture: frame.png, then stage.json
    Preview.Shot shot = Preview.take(this.shown.stageId());
    if (shot != null) {
      this.capture(shot.folder());
      try {
        Preview.write(shot);
      } catch (java.io.IOException e) {
        System.err.println("Scratch for Java: could not write stage.json: " + e.getMessage());
      }
      if (shot.thenExit()) {
        this.close();
      }
    }
  }

  /** A stage's own mouse pointer: made once per image, the system's again for none. */
  private void updateCursor(org.openpatch.scratch.internal.draw.CursorDraw wanted) {
    Image image = wanted == null ? null : wanted.image();
    if (image == this.cursorImage) {
      return;
    }
    this.cursorImage = image;
    long old = this.cursor;
    this.cursor = 0;
    if (image != null) {
      try (MemoryStack stack = MemoryStack.stackPush()) {
        java.nio.ByteBuffer rgba = stack.malloc(image.getWidth() * image.getHeight() * 4);
        for (int p : image.getArgb()) {
          rgba.put((byte) (p >> 16)).put((byte) (p >> 8)).put((byte) p).put((byte) (p >>> 24));
        }
        rgba.flip();
        org.lwjgl.glfw.GLFWImage glfwImage = org.lwjgl.glfw.GLFWImage.malloc(stack);
        glfwImage.set(image.getWidth(), image.getHeight(), rgba);
        this.cursor = glfwCreateCursor(glfwImage, wanted.hotX(), wanted.hotY());
      }
    }
    glfwSetCursor(this.handle, this.cursor);
    if (old != 0) {
      glfwDestroyCursor(old);
    }
  }

  /** frame.png at stage size, as Studio's preview and capture expect. */
  private void capture(Path folder) {
    try {
      Files.createDirectories(folder);
      int[] pixels = this.renderer.capture(this.shown);
      new Image(this.shown.width(), this.shown.height(), pixels).writePng(folder.resolve("frame.png"));
    } catch (Exception e) {
      System.err.println("Scratch for Java: could not capture the frame: " + e.getMessage());
    }
  }

  // ---- input --------------------------------------------------------------------------------

  private void installInput() {
    glfwSetKeyCallback(this.handle, (win, key, scancode, action, mods) -> {
      if (action == GLFW_REPEAT) {
        // one whenKeyPressed per press, as Processing had it; the ask() box still repeats
        KeyCode code = Keys.map(key, scancode);
        if (code != KeyCode.UNKNOWN) {
          this.game.post(new InputEvent.Repeat(code));
        }
        return;
      }
      boolean pressed = action == GLFW_PRESS;
      if (pressed && key == GLFW_KEY_F12) {
        this.game.setDebug(!this.game.isDebug());
        return;
      }
      if (pressed && (key == GLFW_KEY_F11 || key == GLFW_KEY_ENTER && (mods & GLFW_MOD_ALT) != 0)) {
        this.toggleFullScreen();
        return;
      }
      KeyCode code = Keys.map(key, scancode);
      if (code != KeyCode.UNKNOWN) {
        this.game.post(new InputEvent.Key(code, pressed));
      }
    });
    glfwSetCharCallback(this.handle, (win, codepoint) -> this.game.post(new InputEvent.Typed(codepoint)));
    glfwSetCursorPosCallback(this.handle, (win, x, y) -> {
      this.cursorX = x;
      this.cursorY = y;
      double[] p = this.toStage(x, y);
      this.game.post(new InputEvent.Move(p[0], p[1]));
    });
    glfwSetMouseButtonCallback(this.handle, (win, button, action, mods) -> {
      MouseCode code = switch (button) {
        case GLFW_MOUSE_BUTTON_LEFT -> MouseCode.LEFT;
        case GLFW_MOUSE_BUTTON_RIGHT -> MouseCode.RIGHT;
        case GLFW_MOUSE_BUTTON_MIDDLE -> MouseCode.CENTER;
        default -> null;
      };
      if (code != null) {
        double[] p = this.toStage(this.cursorX, this.cursorY);
        this.game.post(new InputEvent.Button(code, action == GLFW_PRESS, p[0], p[1]));
      }
    });
    // positive towards the user, as 6.x had it; GLFW counts away from the user
    glfwSetScrollCallback(this.handle, (win, dx, dy) -> this.game.post(new InputEvent.Wheel(-dy)));
  }

  /** Window coordinates (screen units, y down) to stage coordinates through the letterbox. */
  private double[] toStage(double x, double y) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      var ww = stack.mallocInt(1);
      var wh = stack.mallocInt(1);
      var fw = stack.mallocInt(1);
      var fh = stack.mallocInt(1);
      glfwGetWindowSize(this.handle, ww, wh);
      glfwGetFramebufferSize(this.handle, fw, fh);
      // the letterbox is computed in framebuffer pixels; the cursor comes in window units
      double toFb = ww.get(0) == 0 ? 1 : (double) fw.get(0) / ww.get(0);
      int sw = this.game.getWidth(), sh = this.game.getHeight();
      double scale = Math.min((double) fw.get(0) / sw, (double) fh.get(0) / sh);
      if (this.settings.pixelArt() && scale >= 1) {
        scale = Math.floor(scale);
      }
      double left = Math.floor((fw.get(0) - sw * scale) / 2), top = Math.floor((fh.get(0) - sh * scale) / 2);
      double sx = (x * toFb - left) / scale - sw / 2.0, sy = sh / 2.0 - (y * toFb - top) / scale;
      return new double[] {sx, sy};
    }
  }

  /** Borderless window at the screen's size, and back. */
  private void toggleFullScreen() {
    long monitor = glfwGetPrimaryMonitor();
    GLFWVidMode mode = glfwGetVideoMode(monitor);
    if (mode == null) {
      return;
    }
    try (MemoryStack stack = MemoryStack.stackPush()) {
      if (!this.fullScreen) {
        var x = stack.mallocInt(1);
        var y = stack.mallocInt(1);
        var w = stack.mallocInt(1);
        var h = stack.mallocInt(1);
        glfwGetWindowPos(this.handle, x, y);
        glfwGetWindowSize(this.handle, w, h);
        this.windowedBounds = new int[] {x.get(0), y.get(0), w.get(0), h.get(0)};
        glfwSetWindowMonitor(this.handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
      } else {
        int[] b = this.windowedBounds != null ? this.windowedBounds
            : new int[] {100, 100, this.game.getWidth(), this.game.getHeight()};
        glfwSetWindowMonitor(this.handle, 0, b[0], b[1], b[2], b[3], GLFW_DONT_CARE);
      }
    }
    this.fullScreen = !this.fullScreen;
    glfwSwapInterval(this.vsync ? 1 : 0);
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public String toString() {
    return String.format(Locale.ROOT, "WindowHost[%dx%d]", this.game.getWidth(), this.game.getHeight());
  }
}
