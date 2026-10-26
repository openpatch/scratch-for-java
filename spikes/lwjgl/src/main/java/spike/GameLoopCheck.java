package spike;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;
import spike.learner.MySprite;

/**
 * The 7.0 game loop: a window thread that owns GLFW
 * and OpenGL, a game thread that runs the ticks, both in lockstep; a watchdog
 * for learner code that does not return; pen strokes on a CPU canvas whose
 * changed region is uploaded; the renderer sees nothing but the draw list.
 *
 * <p>System properties: {@code spike.hangAt=N} (learner loops forever from
 * tick N), {@code spike.vsync=true}, {@code spike.exitAfter=N} seconds,
 * {@code spike.platform=x11}, {@code spike.speed=1.0} game speed.
 */
public class GameLoopCheck {

  public static final int MAX_TICKS_PER_FRAME = 8;
  static final String LEARNER_PACKAGE = "spike.learner.";

  /** What the renderer gets: nothing else of the scene. */
  record SpriteDraw(double x, double y, double direction) {}

  record CanvasRegion(int x, int y, int width, int height, int[] argb) {}

  record DrawList(long tick, List<SpriteDraw> sprites, CanvasRegion canvasChange) {}

  private final int stageWidth, stageHeight;
  private long window;

  // window thread -> game thread: number of ticks to run, or EXIT
  private static final int EXIT = -1;
  private final BlockingQueue<Integer> batches = new LinkedBlockingQueue<>();
  private final BlockingQueue<DrawList> drawLists = new LinkedBlockingQueue<>();
  private final CountDownLatch exited = new CountDownLatch(1);
  private Thread gameThread;
  private volatile long batchStarted; // 0 while the game thread waits

  // game thread only
  private final MySprite sprite = new MySprite();
  private final Canvas pen;
  private long tick;

  // window thread only
  private int program, vao, vbo, canvasTexture, spriteTexture;
  private DrawList shown;
  private final TickClock clock = new TickClock();

  public static void main(String[] args) {
    long start = System.nanoTime();
    new GameLoopCheck(480, 360);
    System.out.printf("main: returns after %d ms%n", (System.nanoTime() - start) / 1_000_000);
  }

  public GameLoopCheck(int stageWidth, int stageHeight) {
    this.stageWidth = stageWidth;
    this.stageHeight = stageHeight;
    this.pen = new Canvas(stageWidth, stageHeight);
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
    this.clock.gameSpeed = Double.parseDouble(System.getProperty("spike.speed", "1"));
    if (Launcher.isActive()) {
      // Studio's route: the first thread is free for GLFW (-XstartOnFirstThread on macOS)
      Launcher.runWindow(this::windowThread);
    } else {
      if (Platform.get() == Platform.MACOSX) {
        // plain java: GLFW calls are dispatched to the first thread's run loop
        Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
      }
      new Thread(this::windowThread, "scratch4j-window").start();
    }
  }

  // ---- window thread -------------------------------------------------------

  private void windowThread() {
    System.out.println("window: runs on thread '" + Thread.currentThread().getName() + "'");
    this.open();
    this.gameThread = new Thread(this::gameThread, "scratch4j-game");
    this.gameThread.setDaemon(true);
    this.gameThread.start();
    Thread watchdog = new Thread(this::watchdog, "scratch4j-watchdog");
    watchdog.setDaemon(true);
    watchdog.start();

    boolean vsync = Boolean.getBoolean("spike.vsync");
    final long frameNanos = 1_000_000_000L / 60;
    long start = System.nanoTime(), last = start, next = start, second = start;
    int frames = 0;
    int[] ticksPerFrame = new int[MAX_TICKS_PER_FRAME + 1];
    long slowestSwap = 0, slowestWait = 0;

    while (!glfwWindowShouldClose(this.window)) {
      glfwPollEvents();
      long now = System.nanoTime();
      int ticks = this.clock.advance((now - last) / 1e9);
      last = now;
      ticksPerFrame[ticks]++;
      if (ticks > 0) {
        long waitStart = System.nanoTime();
        DrawList list = this.runBatch(ticks);
        slowestWait = Math.max(slowestWait, System.nanoTime() - waitStart);
        if (list == null) {
          break; // closed while the game thread was busy
        }
        this.shown = list;
      }
      this.render();
      // before the swap: afterwards the back buffer is undefined
      if (this.capture != null && this.shown != null && this.shown.tick() == 120) {
        this.capture();
      }
      long swapStart = System.nanoTime();
      glfwSwapBuffers(this.window);
      slowestSwap = Math.max(slowestSwap, System.nanoTime() - swapStart);
      frames++;

      now = System.nanoTime();
      if (now - second >= 1_000_000_000L) {
        System.out.printf(Locale.ROOT,
            "window: %d fps, ticks per frame %s, slowest swap %.1f ms, slowest batch %.1f ms, tick %d%n",
            frames, Arrays.toString(ticksPerFrame), slowestSwap / 1e6, slowestWait / 1e6,
            this.shown == null ? 0 : this.shown.tick());
        frames = 0;
        Arrays.fill(ticksPerFrame, 0);
        slowestSwap = slowestWait = 0;
        second = now;
      }
      this.closeIfTimeIsUp();
      if (!vsync) {
        next += frameNanos;
        long wait = next - System.nanoTime();
        if (wait < -5 * frameNanos) {
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
    this.exit();
  }

  /**
   * Hands a batch to the game thread and waits for its draw list. Polling
   * goes on meanwhile, so the window stays movable and closable even if
   * learner code never returns; nothing is drawn while learner code runs.
   */
  private DrawList runBatch(int ticks) {
    this.batches.add(ticks);
    try {
      while (true) {
        DrawList list = this.drawLists.poll(5, TimeUnit.MILLISECONDS);
        if (list != null) {
          return list;
        }
        glfwPollEvents();
        this.closeIfTimeIsUp();
        if (glfwWindowShouldClose(this.window)) {
          return null;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  private final long started = System.nanoTime();
  private final long exitAfter = Long.getLong("spike.exitAfter", -1) * 1_000_000_000L;

  /** {@code spike.exitAfter}: as if the user closed the window, also while learner code hangs. */
  private void closeIfTimeIsUp() {
    if (this.exitAfter > 0 && System.nanoTime() - this.started > this.exitAfter) {
      glfwSetWindowShouldClose(this.window, true);
    }
  }

  /** whenExits on the game thread, at most 1 s, then the process ends. */
  private void exit() {
    long start = System.nanoTime();
    this.batches.add(EXIT);
    boolean answered;
    try {
      answered = this.exited.await(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      answered = false;
    }
    System.out.printf(Locale.ROOT, "window: closed; game thread %s after %.0f ms%n",
        answered ? "ran whenExits" : "did not answer, skipped", (System.nanoTime() - start) / 1e6);
    glfwDestroyWindow(this.window);
    glfwTerminate();
    System.exit(0);
  }

  // ---- game thread ---------------------------------------------------------

  private void gameThread() {
    try {
      while (true) {
        int ticks = this.batches.take();
        if (ticks == EXIT) {
          this.sprite.whenExits();
          this.exited.countDown();
          return;
        }
        this.batchStarted = System.nanoTime();
        for (int i = 0; i < ticks; i++) {
          // input, timers, broadcasts would come first; then run() in drawing order
          this.sprite.run(this.tick, this.pen);
          this.tick++;
        }
        this.batchStarted = 0;
        this.drawLists.add(new DrawList(this.tick,
            List.of(new SpriteDraw(this.sprite.x, this.sprite.y, this.sprite.direction)),
            this.pen.takeChange()));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /** Names the learner's line when a batch has not come back for 2 s. */
  private void watchdog() {
    long reportedFor = 0;
    while (true) {
      sleep(100);
      long started = this.batchStarted;
      if (started == 0 || started == reportedFor || System.nanoTime() - started < 2_000_000_000L) {
        continue;
      }
      reportedFor = started;
      StackTraceElement[] trace = this.gameThread.getStackTrace();
      List<StackTraceElement> learner = new ArrayList<>();
      for (StackTraceElement e : trace) {
        if (e.getClassName().startsWith(LEARNER_PACKAGE)) {
          learner.add(e);
        }
      }
      StackTraceElement top = learner.isEmpty() ? trace[0] : learner.get(0);
      String simple = top.getClassName().substring(top.getClassName().lastIndexOf('.') + 1);
      System.err.printf("%s.%s (%s:%d) has not returned for 2 seconds. Is there a loop that never ends?%n",
          simple, top.getMethodName(), top.getFileName(), top.getLineNumber());
      for (StackTraceElement e : learner) {
        System.err.println("    at " + e);
      }
    }
  }

  // ---- the CPU canvas (pen and stamps) ---------------------------------------

  /** A stage-sized ARGB canvas, top row first; remembers what changed since the last draw list. */
  public static final class Canvas {
    final int width, height;
    final int[] argb;
    private int changedX0 = Integer.MAX_VALUE, changedY0 = Integer.MAX_VALUE, changedX1 = -1, changedY1 = -1;

    Canvas(int width, int height) {
      this.width = width;
      this.height = height;
      this.argb = new int[width * height];
    }

    /** An anti-aliased line with round caps, in Scratch coordinates (centre, y up). */
    public void line(double x0, double y0, double x1, double y1, double size, int color) {
      double ax = x0 + this.width / 2.0, ay = this.height / 2.0 - y0;
      double bx = x1 + this.width / 2.0, by = this.height / 2.0 - y1;
      double r = size / 2;
      int left = Math.max(0, (int) Math.floor(Math.min(ax, bx) - r - 1));
      int right = Math.min(this.width - 1, (int) Math.ceil(Math.max(ax, bx) + r + 1));
      int top = Math.max(0, (int) Math.floor(Math.min(ay, by) - r - 1));
      int bottom = Math.min(this.height - 1, (int) Math.ceil(Math.max(ay, by) + r + 1));
      double dx = bx - ax, dy = by - ay, length2 = dx * dx + dy * dy;
      int alpha = color >>> 24;
      for (int py = top; py <= bottom; py++) {
        for (int px = left; px <= right; px++) {
          double cx = px + 0.5 - ax, cy = py + 0.5 - ay;
          double t = length2 == 0 ? 0 : Math.max(0, Math.min(1, (cx * dx + cy * dy) / length2));
          double distance = Math.hypot(cx - t * dx, cy - t * dy);
          double coverage = Math.max(0, Math.min(1, r + 0.5 - distance));
          if (coverage > 0) {
            this.blend(py * this.width + px, color, (int) Math.round(alpha * coverage));
          }
        }
      }
      this.changed(left, top, right, bottom);
    }

    public void eraseAll() {
      Arrays.fill(this.argb, 0);
      this.changed(0, 0, this.width - 1, this.height - 1);
    }

    /** Source over, straight alpha. */
    private void blend(int i, int color, int a) {
      int dst = this.argb[i];
      int da = dst >>> 24;
      int outA = a + da * (255 - a) / 255;
      if (outA == 0) {
        this.argb[i] = 0;
        return;
      }
      int r = (((color >> 16) & 0xff) * a + ((dst >> 16) & 0xff) * da * (255 - a) / 255) / outA;
      int g = (((color >> 8) & 0xff) * a + ((dst >> 8) & 0xff) * da * (255 - a) / 255) / outA;
      int b = ((color & 0xff) * a + (dst & 0xff) * da * (255 - a) / 255) / outA;
      this.argb[i] = outA << 24 | r << 16 | g << 8 | b;
    }

    private void changed(int x0, int y0, int x1, int y1) {
      this.changedX0 = Math.min(this.changedX0, x0);
      this.changedY0 = Math.min(this.changedY0, y0);
      this.changedX1 = Math.max(this.changedX1, x1);
      this.changedY1 = Math.max(this.changedY1, y1);
    }

    /** A copy of the region changed since the last call, or null. */
    CanvasRegion takeChange() {
      if (this.changedX1 < 0) {
        return null;
      }
      int w = this.changedX1 - this.changedX0 + 1, h = this.changedY1 - this.changedY0 + 1;
      int[] copy = new int[w * h];
      for (int row = 0; row < h; row++) {
        System.arraycopy(this.argb, (this.changedY0 + row) * this.width + this.changedX0, copy, row * w, w);
      }
      CanvasRegion region = new CanvasRegion(this.changedX0, this.changedY0, w, h, copy);
      this.changedX0 = this.changedY0 = Integer.MAX_VALUE;
      this.changedX1 = this.changedY1 = -1;
      return region;
    }
  }

  // ---- the tick clock --------------------------------------------------------

  /** The tick clock, run once per frame on the window thread. */
  static final class TickClock {
    static final double TICK = 1 / 60.0;
    double gameSpeed = 1;
    double refreshPeriod = TICK;
    private double owed;

    void useRefreshRate(int hz) {
      // a refresh within 0.5 % of 60 Hz counts as exactly 1/60 s
      this.refreshPeriod = hz <= 0 || Math.abs(hz - 60) / 60.0 <= 0.005 ? TICK : 1.0 / hz;
    }

    int advance(double elapsed) {
      elapsed = Math.min(elapsed, 0.25);
      elapsed = this.snap(elapsed);
      this.owed += elapsed * this.gameSpeed;
      // the epsilon keeps 1/60 + 1/60 + ... from landing a hair below a whole tick
      int ticks = (int) Math.min(Math.floor(this.owed * 60 + 1e-9), MAX_TICKS_PER_FRAME);
      this.owed = Math.max(0, this.owed - ticks * TICK);
      if (ticks == MAX_TICKS_PER_FRAME) {
        this.owed = Math.min(this.owed, TICK);
      }
      return ticks;
    }

    /** Near a whole number of refresh periods: exactly that many. */
    double snap(double elapsed) {
      long periods = Math.round(elapsed / this.refreshPeriod);
      if (periods >= 1 && Math.abs(elapsed - periods * this.refreshPeriod) < 0.1 * this.refreshPeriod) {
        return periods * this.refreshPeriod;
      }
      return elapsed;
    }
  }

  // ---- rendering: draw list in, pixels out ------------------------------------

  private void open() {
    GLFWErrorCallback.createPrint(System.err).set();
    if ("x11".equals(System.getProperty("spike.platform"))) {
      glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);
    }
    if (!glfwInit()) {
      throw new IllegalStateException("GLFW could not start");
    }
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    this.window = glfwCreateWindow(this.stageWidth, this.stageHeight, "Scratch for Java 7 game loop", 0, 0);
    if (this.window == 0) {
      throw new IllegalStateException("no window: OpenGL 3.3 core is not available");
    }
    glfwMakeContextCurrent(this.window);
    GL.createCapabilities();
    glfwSwapInterval(Boolean.getBoolean("spike.vsync") ? 1 : 0);
    GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    if (mode != null) {
      this.clock.useRefreshRate(mode.refreshRate());
      System.out.println("window: refresh rate " + mode.refreshRate() + " Hz");
    }

    this.program = SpikeShaders.program("""
        #version 330 core
        layout(location = 0) in vec2 position;
        layout(location = 1) in vec2 texCoord;
        uniform mat3 transform;
        out vec2 uv;
        void main() {
          gl_Position = vec4((transform * vec3(position, 1.0)).xy, 0.0, 1.0);
          uv = texCoord;
        }
        """, """
        #version 330 core
        uniform sampler2D image;
        in vec2 uv;
        out vec4 color;
        void main() { color = texture(image, uv); }
        """);
    this.vao = glGenVertexArrays();
    glBindVertexArray(this.vao);
    this.vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
    glBufferData(GL_ARRAY_BUFFER, 24 * Float.BYTES, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
    glEnableVertexAttribArray(1);

    this.canvasTexture = texture(this.stageWidth, this.stageHeight, new int[this.stageWidth * this.stageHeight]);
    int[] dot = new int[32 * 32];
    for (int i = 0; i < dot.length; i++) {
      double d = Math.hypot(i % 32 - 15.5, i / 32 - 15.5);
      dot[i] = d < 15 ? (i % 32 > 20 ? 0xff222222 : 0xffffaa00) : 0;
    }
    this.spriteTexture = texture(32, 32, dot);
    glEnable(GL_BLEND);
    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
  }

  /** ARGB ints go up as they are: BGRA with reversed 8-bit order is ARGB in an int. */
  private static int texture(int w, int h, int[] argb) {
    int t = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, t);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, argb);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    return t;
  }

  private long uploadedPixels, uploads;
  private String capture = System.getProperty("spike.capture");

  /** The framebuffer as it was presented, for CI runs nobody watches. */
  private void capture() {
    int[] fw = new int[1], fh = new int[1];
    glfwGetFramebufferSize(this.window, fw, fh);
    java.nio.ByteBuffer rgba = BufferUtils.createByteBuffer(fw[0] * fh[0] * 4);
    glReadBuffer(GL_BACK);
    glReadPixels(0, 0, fw[0], fh[0], GL_RGBA, GL_UNSIGNED_BYTE, rgba);
    org.lwjgl.stb.STBImageWrite.stbi_flip_vertically_on_write(true);
    boolean ok = org.lwjgl.stb.STBImageWrite.stbi_write_png(this.capture, fw[0], fh[0], 4, rgba, fw[0] * 4);
    System.out.println("window: captured " + fw[0] + "x" + fh[0] + " to " + this.capture + (ok ? "" : " FAILED"));
    this.capture = null;
  }

  private void render() {
    int[] fw = new int[1], fh = new int[1];
    glfwGetFramebufferSize(this.window, fw, fh);
    float ratio = Math.min((float) fw[0] / this.stageWidth, (float) fh[0] / this.stageHeight);
    int w = Math.round(this.stageWidth * ratio), h = Math.round(this.stageHeight * ratio);
    glViewport(0, 0, fw[0], fh[0]);
    glClearColor(0, 0, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
    glViewport((fw[0] - w) / 2, (fh[0] - h) / 2, w, h);
    glEnable(GL_SCISSOR_TEST);
    glScissor((fw[0] - w) / 2, (fh[0] - h) / 2, w, h);
    glClearColor(1, 1, 1, 1);
    glClear(GL_COLOR_BUFFER_BIT);
    glDisable(GL_SCISSOR_TEST);
    if (this.shown == null) {
      return;
    }
    CanvasRegion change = this.shown.canvasChange();
    if (change != null) {
      // only what changed goes up, as the plan wants for pen and stamps
      glBindTexture(GL_TEXTURE_2D, this.canvasTexture);
      glTexSubImage2D(GL_TEXTURE_2D, 0, change.x(), change.y(), change.width(), change.height(), GL_BGRA,
          GL_UNSIGNED_INT_8_8_8_8_REV, change.argb());
      this.uploadedPixels += (long) change.width() * change.height();
      this.uploads++;
      if (this.uploads % 600 == 0) {
        System.out.printf(Locale.ROOT, "window: canvas uploads average %.0f pixels (stage has %d)%n",
            (double) this.uploadedPixels / this.uploads, this.stageWidth * this.stageHeight);
      }
      // a draw list is shown more than once when a frame has no tick: upload it once
      this.shown = new DrawList(this.shown.tick(), this.shown.sprites(), null);
    }
    glUseProgram(this.program);
    this.quad(this.canvasTexture, 0, 0, 0, this.stageWidth, this.stageHeight);
    for (SpriteDraw s : this.shown.sprites()) {
      this.quad(this.spriteTexture, s.x(), s.y(), s.direction() - 90, 32, 32);
    }
  }

  private void quad(int texture, double x, double y, double degrees, float width, float height) {
    double r = Math.toRadians(-degrees);
    float c = (float) Math.cos(r), s = (float) Math.sin(r);
    float sx = 2f / this.stageWidth, sy = 2f / this.stageHeight;
    FloatBuffer m = BufferUtils.createFloatBuffer(9).put(new float[] {
        c * sx, s * sy, 0, -s * sx, c * sy, 0, (float) x * sx, (float) y * sy, 1}).flip();
    glUniformMatrix3fv(glGetUniformLocation(this.program, "transform"), false, m);
    glBindTexture(GL_TEXTURE_2D, texture);
    float hw = width / 2, hh = height / 2;
    glBindVertexArray(this.vao);
    glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, new float[] {
        -hw, hh, 0, 0, hw, hh, 1, 0, hw, -hh, 1, 1, -hw, hh, 0, 0, hw, -hh, 1, 1, -hw, -hh, 0, 1});
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
