package spike;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;

/**
 * The render loop the library would run: its own thread, a stage-sized
 * multisampled layer letterboxed into the window, 60 frames a second, input
 * queued and handled at the start of a frame.
 */
public class SpikeWindow {

  private static final String VERTEX = """
      #version 330 core
      layout(location = 0) in vec2 position;
      layout(location = 1) in vec2 texCoord;
      uniform mat3 transform;
      out vec2 uv;
      void main() {
        vec3 p = transform * vec3(position, 1.0);
        gl_Position = vec4(p.xy, 0.0, 1.0);
        uv = texCoord;
      }
      """;

  /** What every shader gets in front of the student's body. */
  private static final String FRAGMENT_HEADER = """
      #version 330 core
      uniform sampler2D costume;
      uniform vec2 resolution;
      uniform float time;
      in vec2 uv;
      out vec4 color;
      #line 1
      """;

  private static final String PLAIN_BODY = """
      void main() {
        color = texture(costume, uv);
      }
      """;

  private final int stageWidth;
  private final int stageHeight;
  private final CountDownLatch ready = new CountDownLatch(1);
  private final ConcurrentLinkedQueue<Runnable> events = new ConcurrentLinkedQueue<>();

  private long window;
  private int msaaFbo, resolveFbo, resolveTexture;
  private int vao, vbo;
  private int plainProgram, studentProgram;
  private int costume, costumeWidth, costumeHeight;
  private boolean useStudentShader;
  private double pressX, pressY;
  private double mouseX, mouseY;

  public SpikeWindow(int stageWidth, int stageHeight) {
    this.stageWidth = stageWidth;
    this.stageHeight = stageHeight;
    // LWJGL's default backend uses sun.misc.Unsafe, which Java 24+ warns about
    // in red. Its FFM backend (in the jar's META-INF/versions/25) does not.
    // Has to be set before the first LWJGL class that touches memory loads.
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
    if (Platform.get() == Platform.MACOSX) {
      // GLFW calls are dispatched to the first thread, which the java launcher
      // keeps in a run loop when -XstartOnFirstThread is not given
      Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
    }
    Thread loop = new Thread(this::run, "scratch4j-render");
    loop.start();
  }

  public void awaitReady() throws InterruptedException {
    this.ready.await();
  }

  private void run() {
    try {
      this.open();
      this.ready.countDown();
      this.loop();
    } catch (Throwable t) {
      t.printStackTrace();
      this.ready.countDown();
    } finally {
      if (this.window != 0) {
        glfwDestroyWindow(this.window);
      }
      glfwTerminate();
    }
  }

  private void open() throws IOException {
    GLFWErrorCallback.createPrint(System.err).set();
    // GLFW 3.4 prefers Wayland whenever WAYLAND_DISPLAY is set - also inside
    // xvfb-run, which then no longer keeps the window off the desktop
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
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
    boolean hidden = Boolean.getBoolean("spike.hidden");
    glfwWindowHint(GLFW_VISIBLE, hidden ? GLFW_FALSE : GLFW_TRUE);
    this.window = glfwCreateWindow(this.stageWidth, this.stageHeight, "Scratch for Java 7 spike", 0, 0);
    if (this.window == 0) {
      throw new IllegalStateException("no window: OpenGL 3.3 core is not available");
    }
    glfwMakeContextCurrent(this.window);
    GL.createCapabilities();
    glfwSwapInterval(Boolean.getBoolean("spike.vsync") ? 1 : 0);

    int[] w = new int[1], h = new int[1], fw = new int[1], fh = new int[1];
    float[] sx = new float[1], sy = new float[1];
    glfwGetWindowSize(this.window, w, h);
    glfwGetFramebufferSize(this.window, fw, fh);
    glfwGetWindowContentScale(this.window, sx, sy);
    System.out.printf(Locale.ROOT, "render: LWJGL %s, GLFW platform %s, Java %s%n", Version.getVersion(),
        platformName(glfwGetPlatform()), System.getProperty("java.version"));
    System.out.printf("render: GL %s / %s / GLSL %s%n", glGetString(GL_VERSION), glGetString(GL_RENDERER),
        glGetString(GL_SHADING_LANGUAGE_VERSION));
    System.out.printf(Locale.ROOT, "render: window %dx%d, framebuffer %dx%d, content scale %.2f x %.2f%n",
        w[0], h[0], fw[0], fh[0], sx[0], sy[0]);

    this.createLayer();
    this.createQuad();
    this.plainProgram = program(PLAIN_BODY, "plain");
    String shaderPath = System.getProperty("spike.shader", "src/main/resources/pixelate.frag");
    this.studentProgram = program(Files.readString(Path.of(shaderPath)), shaderPath);
    this.loadCostume(System.getProperty("spike.costume", "../../src/examples/java/demos/shader/cat.png"));
    this.installInput();
    this.useStudentShader = Boolean.getBoolean("spike.studentShader");

    glEnable(GL_BLEND);
    // what P2D uses: colour blended by alpha, alpha accumulated
    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
  }

  private void loop() throws IOException {
    final long frameNanos = 1_000_000_000L / 60;
    long exitAfter = Long.getLong("spike.exitAfter", -1) * 1_000_000_000L;
    String capture = System.getProperty("spike.capture");
    long start = System.nanoTime();
    long next = start;
    long second = start;
    int frames = 0;
    long frame = 0;
    while (!glfwWindowShouldClose(this.window)) {
      glfwPollEvents();
      // events of this frame are handled before anything runs or draws
      for (Runnable event; (event = this.events.poll()) != null; ) {
        event.run();
      }
      double time = (System.nanoTime() - start) / 1e9;
      this.drawStage(time);
      this.present();
      glfwSwapBuffers(this.window);
      frame++;
      frames++;

      if (capture != null && frame == 60) {
        this.capture(capture);
        System.out.println("render: stage saved to " + capture);
      }
      long now = System.nanoTime();
      if (now - second >= 1_000_000_000L) {
        System.out.printf("render: %d fps%n", frames);
        frames = 0;
        second = now;
      }
      if (exitAfter > 0 && now - start > exitAfter) {
        glfwSetWindowShouldClose(this.window, true);
      }
      next += frameNanos;
      long wait = next - System.nanoTime();
      if (wait < -5 * frameNanos) {
        next = System.nanoTime(); // far behind (a breakpoint, a slow frame): do not race to catch up
      } else if (wait > 0) {
        if (wait > 2_000_000) {
          try {
            Thread.sleep((wait - 1_000_000) / 1_000_000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        while (System.nanoTime() < next) {
          Thread.onSpinWait();
        }
      }
    }
    System.out.println("render: window closed after " + frame + " frames");
  }

  // ---- the stage layer -----------------------------------------------------

  private void createLayer() {
    int samples = 4;
    this.msaaFbo = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, this.msaaFbo);
    int colour = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, colour);
    glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, this.stageWidth, this.stageHeight);
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colour);
    check("multisampled layer");

    this.resolveFbo = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, this.resolveFbo);
    this.resolveTexture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, this.resolveTexture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.stageWidth, this.stageHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE,
        (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.resolveTexture, 0);
    check("resolved layer");
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  private static void check(String what) {
    int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
      throw new IllegalStateException(what + " incomplete: 0x" + Integer.toHexString(status));
    }
  }

  private void createQuad() {
    this.vao = glGenVertexArrays();
    glBindVertexArray(this.vao);
    this.vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
    glBufferData(GL_ARRAY_BUFFER, 6 * 4 * Float.BYTES, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
    glEnableVertexAttribArray(1);
  }

  private void drawStage(double time) {
    glBindFramebuffer(GL_FRAMEBUFFER, this.msaaFbo);
    glViewport(0, 0, this.stageWidth, this.stageHeight);
    glClearColor(1, 1, 1, 1);
    glClear(GL_COLOR_BUFFER_BIT);

    // a cat that circles and turns, in Scratch coordinates (origin centre, y up)
    double x = Math.cos(time) * 120;
    double y = Math.sin(time) * 80;
    double degrees = time * 45;
    int program = this.useStudentShader ? this.studentProgram : this.plainProgram;
    glUseProgram(program);
    glUniform2f(glGetUniformLocation(program, "resolution"), this.costumeWidth, this.costumeHeight);
    glUniform1f(glGetUniformLocation(program, "time"), (float) time);
    glUniform1i(glGetUniformLocation(program, "costume"), 0);
    glUniformMatrix3fv(glGetUniformLocation(program, "transform"), false, this.transform(x, y, degrees));
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, this.costume);
    float hw = this.costumeWidth / 2f, hh = this.costumeHeight / 2f;
    FloatBuffer quad = BufferUtils.createFloatBuffer(24).put(new float[] {
        -hw, hh, 0, 0, hw, hh, 1, 0, hw, -hh, 1, 1,
        -hw, hh, 0, 0, hw, -hh, 1, 1, -hw, -hh, 0, 1}).flip();
    glBindVertexArray(this.vao);
    glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, quad);
    glDrawArrays(GL_TRIANGLES, 0, 6);

    glBindFramebuffer(GL_READ_FRAMEBUFFER, this.msaaFbo);
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.resolveFbo);
    glBlitFramebuffer(0, 0, this.stageWidth, this.stageHeight, 0, 0, this.stageWidth, this.stageHeight,
        GL_COLOR_BUFFER_BIT, GL_NEAREST);
  }

  /** Costume space to clip space: rotate (Scratch: clockwise), move, scale to the stage. */
  private FloatBuffer transform(double x, double y, double degrees) {
    double r = Math.toRadians(-degrees);
    float c = (float) Math.cos(r), s = (float) Math.sin(r);
    float sx = 2f / this.stageWidth, sy = 2f / this.stageHeight;
    // column-major mat3
    return BufferUtils.createFloatBuffer(9).put(new float[] {
        c * sx, s * sy, 0,
        -s * sx, c * sy, 0,
        (float) x * sx, (float) y * sy, 1}).flip();
  }

  /** The stage letterboxed into whatever size the window's framebuffer has. */
  private void present() {
    int[] fw = new int[1], fh = new int[1];
    glfwGetFramebufferSize(this.window, fw, fh);
    float ratio = Math.min((float) fw[0] / this.stageWidth, (float) fh[0] / this.stageHeight);
    int w = Math.round(this.stageWidth * ratio), h = Math.round(this.stageHeight * ratio);
    int x = (fw[0] - w) / 2, y = (fh[0] - h) / 2;
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    glViewport(0, 0, fw[0], fh[0]);
    glClearColor(0, 0, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, this.resolveFbo);
    glBlitFramebuffer(0, 0, this.stageWidth, this.stageHeight, x, y, x + w, y + h, GL_COLOR_BUFFER_BIT,
        GL_LINEAR);
  }

  /** The stage as the library's Pixels hands it out: ARGB, top row first. */
  private void capture(String file) throws IOException {
    ByteBuffer rgba = BufferUtils.createByteBuffer(this.stageWidth * this.stageHeight * 4);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, this.resolveFbo);
    glReadPixels(0, 0, this.stageWidth, this.stageHeight, GL_RGBA, GL_UNSIGNED_BYTE, rgba);
    int[] argb = new int[this.stageWidth * this.stageHeight];
    for (int row = 0; row < this.stageHeight; row++) {
      int from = (this.stageHeight - 1 - row) * this.stageWidth * 4;
      for (int col = 0; col < this.stageWidth; col++) {
        int i = from + col * 4;
        argb[row * this.stageWidth + col] = (rgba.get(i + 3) & 0xff) << 24 | (rgba.get(i) & 0xff) << 16
            | (rgba.get(i + 1) & 0xff) << 8 | (rgba.get(i + 2) & 0xff);
      }
    }
    BufferedImage image = new BufferedImage(this.stageWidth, this.stageHeight, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, this.stageWidth, this.stageHeight, argb, 0, this.stageWidth);
    File out = new File(file);
    if (out.getParentFile() != null) {
      out.getParentFile().mkdirs();
    }
    ImageIO.write(image, "png", out);
  }

  // ---- assets --------------------------------------------------------------

  private void loadCostume(String path) throws IOException {
    BufferedImage image = ImageIO.read(new File(path));
    this.costumeWidth = image.getWidth();
    this.costumeHeight = image.getHeight();
    int[] argb = image.getRGB(0, 0, this.costumeWidth, this.costumeHeight, null, 0, this.costumeWidth);
    ByteBuffer rgba = BufferUtils.createByteBuffer(argb.length * 4);
    for (int p : argb) {
      rgba.put((byte) (p >> 16)).put((byte) (p >> 8)).put((byte) p).put((byte) (p >>> 24));
    }
    rgba.flip();
    this.costume = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, this.costume);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.costumeWidth, this.costumeHeight, 0, GL_RGBA,
        GL_UNSIGNED_BYTE, rgba);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  }

  private static int program(String body, String name) {
    int vertex = compile(GL_VERTEX_SHADER, VERTEX, "vertex");
    int fragment = compile(GL_FRAGMENT_SHADER, FRAGMENT_HEADER + body, name);
    int program = glCreateProgram();
    glAttachShader(program, vertex);
    glAttachShader(program, fragment);
    glLinkProgram(program);
    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
      throw new IllegalStateException(name + " does not link:\n" + glGetProgramInfoLog(program));
    }
    return program;
  }

  private static int compile(int type, String source, String name) {
    int shader = glCreateShader(type);
    glShaderSource(shader, source);
    glCompileShader(shader);
    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      // thanks to #line 1 the line numbers are the student's
      throw new IllegalStateException(name + " does not compile:\n" + glGetShaderInfoLog(shader));
    }
    return shader;
  }

  // ---- input ---------------------------------------------------------------

  private void installInput() {
    glfwSetKeyCallback(this.window, (w, key, scancode, action, mods) -> {
      if (action == GLFW_REPEAT) {
        return; // Processing ignores auto-repeat: one whenKeyPressed per press
      }
      String name = glfwGetKeyName(key, scancode);
      int vk = KeyMapping.toVirtualKey(key, name);
      this.events.add(() -> {
        System.out.printf("input: %s key=%d scancode=%d layout name=%s -> KeyCode %d%n",
            action == GLFW_PRESS ? "press  " : "release", key, scancode, name, vk);
        if (action == GLFW_PRESS && vk == 'P') {
          this.useStudentShader = !this.useStudentShader;
          System.out.println("input: student shader " + (this.useStudentShader ? "on" : "off"));
        }
      });
    });
    glfwSetCharCallback(this.window, (w, codepoint) -> this.events.add(() -> System.out.printf(
        "input: typed '%s' (U+%04X)%n", new String(Character.toChars(codepoint)), codepoint)));
    glfwSetCursorPosCallback(this.window, (w, x, y) -> {
      this.mouseX = x;
      this.mouseY = y;
    });
    glfwSetMouseButtonCallback(this.window, (w, button, action, mods) -> {
      double x = this.mouseX, y = this.mouseY;
      if (action == GLFW_PRESS) {
        this.pressX = x;
        this.pressY = y;
        return;
      }
      // a click: released close to where it was pressed
      boolean click = Math.hypot(x - this.pressX, y - this.pressY) < 4;
      double[] stage = this.toStage(x, y);
      this.events.add(() -> System.out.printf(Locale.ROOT, "input: %s button %d at stage (%.0f, %.0f)%n",
          click ? "click" : "drag-release", button, stage[0], stage[1]));
    });
    glfwSetScrollCallback(this.window, (w, dx, dy) -> this.events.add(
        () -> System.out.printf(Locale.ROOT, "input: wheel %.1f%n", dy)));
  }

  /** Window coordinates (screen units, y down) to Scratch coordinates through the letterbox. */
  private double[] toStage(double x, double y) {
    int[] w = new int[1], h = new int[1];
    glfwGetWindowSize(this.window, w, h);
    double ratio = Math.min((double) w[0] / this.stageWidth, (double) h[0] / this.stageHeight);
    double left = (w[0] - this.stageWidth * ratio) / 2, top = (h[0] - this.stageHeight * ratio) / 2;
    return new double[] {(x - left) / ratio - this.stageWidth / 2.0, this.stageHeight / 2.0 - (y - top) / ratio};
  }

  private static String platformName(int platform) {
    return switch (platform) {
      case GLFW_PLATFORM_WIN32 -> "win32";
      case GLFW_PLATFORM_COCOA -> "cocoa";
      case GLFW_PLATFORM_WAYLAND -> "wayland";
      case GLFW_PLATFORM_X11 -> "x11";
      case GLFW_PLATFORM_NULL -> "null";
      default -> "unknown " + platform;
    };
  }
}
