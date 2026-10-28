package org.openpatch.scratch.internal.window;

import static org.openpatch.scratch.internal.platform.GL.*;
import static org.openpatch.scratch.internal.platform.InputCodes.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.openpatch.scratch.KeyCode;
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
import org.openpatch.scratch.internal.platform.Display;
import org.openpatch.scratch.internal.platform.Platform;
import org.openpatch.scratch.internal.render.Renderer;

/**
 * The window: the platform's {@link Display} and GL on the window thread, the game loop in lockstep
 * with the game thread, input turned into events for the game. Which thread the window thread is
 * the platform decides ({@link Display#start}).
 */
public final class WindowHost {

  /** What the window needs to know before it opens. */
  public record Settings(String title, boolean fullScreen, boolean pixelArt, boolean smoothPixelArt,
      double frameSmoothing, String splashLogo, String assets, List<Extension> extensions) {}

  private final Game game;
  private final Window window;
  private final Settings settings;
  private final Display display;
  private final CountDownLatch opened = new CountDownLatch(1);
  private final TickClock clock = new TickClock();
  private volatile boolean closeRequested;
  private boolean windowOpen;
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
  private boolean vsync;
  /** The screen's refresh period: with working vsync, swaps come no faster than this. */
  private long refreshPeriod = 1_000_000_000L / 60;
  /** False once vsync turned out not to wait, or with {@code -Dscratch4j.vsync=false}. */
  private boolean vsyncWorks;
  /** Vsync only while focused (Wayland: a hidden window may block the swap there). */
  private boolean vsyncNeedsFocus;
  private Image cursorImage;
  private long cursor;

  private WindowHost(Game game, Window window, Settings settings) {
    this.game = game;
    this.window = window;
    this.settings = settings;
    this.fullScreen = settings.fullScreen();
    this.display = Platform.get().display();
  }

  /** Opens the window on its thread and returns at once, as 6.x's {@code new Window} did. */
  public static WindowHost start(Game game, Window window, Settings settings) {
    WindowHost host = new WindowHost(game, window, settings);
    game.onExit(window::whenExits);
    host.display.start(host::run);
    return host;
  }

  /** Ends the program as closing the window does: {@code whenExits()} first. Any thread. */
  public void close() {
    this.closeRequested = true;
    this.display.requestClose();
  }

  // ---- the window thread --------------------------------------------------------------------

  private void run() {
    try {
      this.open();
    } catch (Throwable t) {
      System.err.println("Scratch for Java could not open its window: " + t.getMessage()
          + (t.getCause() != null ? " (" + t.getCause().getMessage() + ")" : ""));
      Release.crashed("The game could not open its window. " + t.getMessage());
      this.opened.countDown();
      Platform.get().exit(1);
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
    this.display.destroy();
    Platform.get().exit(0);
  }

  private void open() {
    boolean hidden = System.getProperty("scratch4j.preview") != null || Boolean.getBoolean("scratch4j.hidden");
    int[] size = this.initialSize();
    this.display.open(size[0], size[1], this.settings.title(), !hidden, new Input());
    this.windowOpen = true;
    this.vsyncNeedsFocus = this.display.vsyncNeedsFocus();
    this.vsyncWorks = !"false".equals(System.getProperty("scratch4j.vsync"));
    this.vsync = this.wantsVsync();
    this.display.setSwapInterval(this.vsync ? 1 : 0);
    int refreshRate = this.display.refreshRate();
    this.clock.useRefreshRate(refreshRate);
    if (refreshRate > 0) {
      this.refreshPeriod = 1_000_000_000L / refreshRate;
    }
    this.renderer = new Renderer(this.settings.pixelArt(), this.settings.smoothPixelArt(),
        this.settings.frameSmoothing());
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
      System.out.println("Scratch for Java: " + this.display.describe());
    }
  }

  /** The stage size, or in pixel art the largest whole multiple that fits the screen. */
  private int[] initialSize() {
    int w = this.game.getWidth(), h = this.game.getHeight();
    if (this.settings.pixelArt()) {
      int[] screen = this.display.screenSize();
      if (screen != null) {
        int factor = Math.max(1, Math.min((screen[0] - 80) / w, (screen[1] - 120) / h));
        return new int[] {w * factor, h * factor};
      }
    }
    return new int[] {w, h};
  }

  private void frames() {
    final long period = 1_000_000_000L / 60;
    long last = System.nanoTime(), next = last, second = last, lastSwap = last;
    int frames = 0;
    int swaps = 0, fastSwaps = 0;
    while (!this.display.shouldClose() && !this.closeRequested) {
      this.display.pollEvents();
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
        this.crashed("The game stopped because of an error.\n" + firstLine(this.game.getStopped().message()));
        break;
      }
      this.shown = list;
      this.updateCursor(list.cursor());
      if (this.wantsVsync() != this.vsync) {
        this.vsync = !this.vsync;
        this.display.setSwapInterval(this.vsync ? 1 : 0);
        next = System.nanoTime();
        swaps = 0;
        fastSwaps = 0;
        if (Boolean.getBoolean("scratch4j.debugGl")) {
          System.out.println("Scratch for Java: vsync " + (this.vsync ? "on" : "off (own pacing)"));
        }
      }
      this.draw();
      this.display.swapBuffers();
      frames++;
      long swapped = System.nanoTime();
      if (this.vsync) {
        // vsync that does not wait (Xvfb, some drivers): most swaps of a second come faster than
        // the screen refreshes; then pace ourselves instead (slow frames are no sign either way)
        if (swapped - lastSwap < this.refreshPeriod * 3 / 4) {
          fastSwaps++;
        }
        if (++swaps == 60) {
          if (fastSwaps > 45) {
            this.vsyncWorks = false;
            this.vsync = false;
            this.display.setSwapInterval(0);
            next = swapped;
            if (Boolean.getBoolean("scratch4j.debugGl")) {
              System.out.println("Scratch for Java: vsync does not wait, own pacing");
            }
          }
          swaps = 0;
          fastSwaps = 0;
        }
      }
      lastSwap = swapped;
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
          this.display.waitUntil(next);
        }
      }
    }
  }

  private static String firstLine(String text) {
    String stripped = text.strip();
    int end = stripped.indexOf('\n');
    String line = end < 0 ? stripped : stripped.substring(0, end);
    return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
  }

  /**
   * Vsync where it works; where it needs the focus, only while the window has it. Own pacing at
   * 60 Hz is not locked to the screen's refresh, so it shows a frame twice or skips one now and
   * then — a visible hitch in anything that scrolls.
   */
  private boolean wantsVsync() {
    return this.vsyncWorks && (!this.vsyncNeedsFocus || this.display.isFocused());
  }

  /** The crash box of a released game, with the window out of its way (fullscreen would hide it). */
  private void crashed(String what) {
    if (Release.isOn() && this.windowOpen) {
      this.display.hide();
      this.display.pollEvents();
    }
    Release.crashed(what);
  }

  private void pollWhileWaiting() {
    this.display.pollEvents();
    if (this.display.shouldClose()) {
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
      this.display.swapBuffers();
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
    int[] framebuffer = this.display.framebufferSize();
    this.renderer.render(this.shown, framebuffer[0], framebuffer[1]);
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
    this.cursor = image == null ? 0
        : this.display.createCursor(image.getWidth(), image.getHeight(), image.getArgb(), wanted.hotX(), wanted.hotY());
    this.display.setCursor(this.cursor);
    if (old != 0) {
      this.display.destroyCursor(old);
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

  /** The display's input, turned into the game's events. */
  private final class Input implements Display.Input {

    @Override
    public void key(int key, int scancode, int action, int mods) {
      if (action == REPEAT) {
        // one whenKeyPressed per press, as Processing had it; the ask() box still repeats
        KeyCode code = Keys.map(key, scancode, WindowHost.this.display);
        if (code != KeyCode.UNKNOWN) {
          WindowHost.this.game.post(new InputEvent.Repeat(code));
        }
        return;
      }
      boolean pressed = action == PRESS;
      if (pressed && key == KEY_F12) {
        WindowHost.this.game.setDebug(!WindowHost.this.game.isDebug());
        return;
      }
      if (pressed && (key == KEY_F11 || key == KEY_ENTER && (mods & MOD_ALT) != 0)) {
        WindowHost.this.toggleFullScreen();
        return;
      }
      KeyCode code = Keys.map(key, scancode, WindowHost.this.display);
      if (code != KeyCode.UNKNOWN) {
        WindowHost.this.game.post(new InputEvent.Key(code, pressed));
      }
    }

    @Override
    public void typed(int codepoint) {
      WindowHost.this.game.post(new InputEvent.Typed(codepoint));
    }

    @Override
    public void pointer(double x, double y) {
      WindowHost.this.cursorX = x;
      WindowHost.this.cursorY = y;
      double[] p = WindowHost.this.toStage(x, y);
      WindowHost.this.game.post(new InputEvent.Move(p[0], p[1]));
    }

    @Override
    public void button(int button, int action, int mods) {
      MouseCode code = switch (button) {
        case MOUSE_BUTTON_LEFT -> MouseCode.LEFT;
        case MOUSE_BUTTON_RIGHT -> MouseCode.RIGHT;
        case MOUSE_BUTTON_MIDDLE -> MouseCode.CENTER;
        default -> null;
      };
      if (code != null) {
        double[] p = WindowHost.this.toStage(WindowHost.this.cursorX, WindowHost.this.cursorY);
        WindowHost.this.game.post(new InputEvent.Button(code, action == PRESS, p[0], p[1]));
      }
    }

    @Override
    public void scroll(double dx, double dy) {
      // positive towards the user, as 6.x had it; GLFW counts away from the user
      WindowHost.this.game.post(new InputEvent.Wheel(-dy));
    }
  }

  /** Window coordinates (screen units, y down) to stage coordinates through the letterbox. */
  private double[] toStage(double x, double y) {
    int[] window = this.display.windowSize();
    int[] framebuffer = this.display.framebufferSize();
    // the letterbox is computed in framebuffer pixels; the cursor comes in window units
    double toFb = window[0] == 0 ? 1 : (double) framebuffer[0] / window[0];
    int sw = this.game.getWidth(), sh = this.game.getHeight();
    double scale = Math.min((double) framebuffer[0] / sw, (double) framebuffer[1] / sh);
    // must match Renderer.letterbox: whole numbers only in crisp pixel art
    if (this.settings.pixelArt() && !this.settings.smoothPixelArt() && scale >= 1) {
      scale = Math.floor(scale);
    }
    double left = Math.floor((framebuffer[0] - sw * scale) / 2), top = Math.floor((framebuffer[1] - sh * scale) / 2);
    double sx = (x * toFb - left) / scale - sw / 2.0, sy = sh / 2.0 - (y * toFb - top) / scale;
    return new double[] {sx, sy};
  }

  /** Borderless window at the screen's size, and back. */
  private void toggleFullScreen() {
    if (this.display.setFullScreen(!this.fullScreen)) {
      this.fullScreen = !this.fullScreen;
      this.display.setSwapInterval(this.vsync ? 1 : 0);
    }
  }

  @Override
  public String toString() {
    return String.format(Locale.ROOT, "WindowHost[%dx%d]", this.game.getWidth(), this.game.getHeight());
  }
}
