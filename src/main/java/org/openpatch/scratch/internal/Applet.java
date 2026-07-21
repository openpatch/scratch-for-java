package org.openpatch.scratch.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Text;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;

/**
 * The Applet class represents the main application window. It is responsible
 * for loading assets,
 * managing stages, and handling mouse and keyboard events.
 */
public class Applet extends PApplet {

  /** Lines containing any of these strings are silently dropped from stderr. */
  private static final String[] SUPPRESSED_STDERR = {
    "The sketch has been resized from",
    "This happened outside Processing",
    "is missing or inaccessible, make sure",
    "X11Util",
    "NamedX11Display",
  };

  private static final class FilteredPrintStream extends PrintStream {
    private final StringBuilder lineBuffer = new StringBuilder();

    FilteredPrintStream(OutputStream out) {
      super(out, true, StandardCharsets.UTF_8);
    }

    private boolean isSuppressed(String line) {
      for (String pattern : SUPPRESSED_STDERR) {
        if (line.contains(pattern)) return true;
      }
      return false;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
      String chunk = new String(buf, off, len, StandardCharsets.UTF_8);
      lineBuffer.append(chunk);
      int newline;
      while ((newline = lineBuffer.indexOf("\n")) >= 0) {
        String line = lineBuffer.substring(0, newline + 1);
        lineBuffer.delete(0, newline + 1);
        if (!isSuppressed(line)) {
          super.write(line.getBytes(StandardCharsets.UTF_8), 0, line.getBytes(StandardCharsets.UTF_8).length);
        }
      }
    }
  }

  enum State {
    LOADING,
    RUNNING,
    TRANSITIONING_OUT,
    TRANSITIONING_IN
  }

  private final int RENDER_HEIGHT;
  private final int RENDER_WIDTH;
  private final boolean FULLSCREEN;
  private State state;

  private boolean debug;
  private static Applet instance;
  private boolean isRunning = false;
  private long numberAssets;
  private long loadedAssets;
  private PImage loading;
  /** How long the loading screen takes to fade in, and again to fade out. */
  private static final int SPLASH_FADE_MS = 300;
  private int splashStart;
  private int splashFadeOutStart;
  private final String assets;
  private Stage stage;
  /** How to reach each stage's render loop, without those methods being public. */
  private final Map<Stage, StageHooks> stageHooks =
      java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
  private int transitionStart;
  private int transitionDuration;
  private Stage transitionToStage;
  private Map<String, Stage> stages;
  private int lastMillis;
  private double deltaTime;
  private String loadingText = "";

  private final AbstractMap<Integer, Boolean> keyCodePressed = new ConcurrentHashMap<>();
  private boolean mouseDown;
  private boolean isSetup;

  /**
   * Constructs an Applet with the specified width, height, fullscreen mode, and
   * assets path.
   *
   * @param width      the width of the window
   * @param height     the height of the window
   * @param fullscreen whether the window should be fullscreen
   * @param assets     the path to the assets directory
   */
  public Applet(int width, final int height, final boolean fullscreen, final String assets) {
    System.setErr(new FilteredPrintStream(System.err));
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.setErr(new PrintStream(OutputStream.nullOutputStream()));
    }, "suppress-jogl-shutdown-noise"));
    this.state = State.LOADING;
    if (height == 0) {
      this.RENDER_HEIGHT = 1080;
    } else {
      this.RENDER_HEIGHT = height;
    }
    if (width == 0) {
      this.RENDER_WIDTH = 1920;
    } else {
      this.RENDER_WIDTH = width;
    }
    this.FULLSCREEN = fullscreen;
    this.assets = assets;

    this.stages = new ConcurrentHashMap<>();

    this.registerMethod("mouseEvent", this);
    this.registerMethod("keyEvent", this);
    if (Applet.instance == null) {
      Applet.instance = this;
    }
    this.runSketch();
    this.thread("loadAssets");
  }

  /**
   * Returns the instance of the application.
   *
   * @return the instance of the application
   */
  public static Applet getInstance() {
    return instance;
  }

  /**
   * Enables or disables the debug mode for the application.
   *
   * @param debug a boolean value where {@code true} enables debug mode and
   *              {@code false} disables
   *              it.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * Checks if the application is in debug mode.
   *
   * @return true if the application is in debug mode, false otherwise.
   */
  public boolean isDebug() {
    return this.debug;
  }

  /**
   * Returns the width of the current window.
   *
   * @return the width of the window in pixels
   */
  public int getWidth() {
    return this.width;
  }

  public int getRenderWidth() {
    return this.RENDER_WIDTH;
  }

  public int getRenderHeight() {
    return this.RENDER_HEIGHT;
  }

  /**
   * Returns the height of the current window.
   *
   * @return the height of the window in pixels
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Returns the current text size.
   *
   * @return the current text size
   */
  public double getTextSize() {
    return this.g.textSize;
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name  Name of the stage
   * @param stage A stage object
   */
  @Deprecated(since = "4.0.0")
  public void addStage(String name, Stage stage) {
    this.stages.put(name, stage);
    if (this.stage == null) {
      this.stage = stage;
    }
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void switchStage(String name) {
    this.stage = this.stages.getOrDefault(name, this.stage);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void removeStage(String name) {
    this.stages.remove(name);
  }

  /**
   * Returns the current stage of the application.
   *
   * @return the current stage
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * Sets the current stage of the application.
   *
   * @param stage the new stage to be set
   */
  /**
   * Registers how the render loop reaches a stage. Called by the stage itself
   * when it is created.
   *
   * @param stage the stage
   * @param hooks the stage's render-loop entry points
   */
  public void registerHooks(Stage stage, StageHooks hooks) {
    this.stageHooks.put(stage, hooks);
  }

  private StageHooks hooksFor(Stage stage) {
    return stage == null ? null : this.stageHooks.get(stage);
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void transitionToStage(Stage stage, int duration) {
    if (this.state == State.RUNNING) {
      this.transitionStart = millis();
      this.transitionDuration = duration;
      this.state = State.TRANSITIONING_OUT;
      this.transitionToStage = stage;
    }
  }

  /**
   * Use this method to call size().
   *
   * @see PApplet#fullScreen()
   * @see PApplet#setup()
   * @see PApplet#size(int,int)
   * @see PApplet#smooth()
   */
  public void settings() {
    if (this.FULLSCREEN) {
      this.fullScreen(P2D);
    } else {
      this.size(this.RENDER_WIDTH, this.RENDER_HEIGHT, P2D);
    }
  }

  /**
   * Returns the time since the last frame in seconds.
   *
   * @return the time since the last frame in seconds
   */
  public double getDeltaTime() {
    return deltaTime;
  }

  /** Pauses the sketch. */
  public void pauseSketch() {
    this.noLoop();
  }

  /** Resumes the sketch. */
  public void resumeSketch() {
    this.loop();
  }

  /** Forces a redraw, even if the sketch is paused. */
  public void redrawSketch() {
    this.redraw();
  }

  public void runSketch() {
    if (!this.isRunning) {
      super.runSketch();
      this.isRunning = true;
    }
  }

  /** Sets up the application window. */
  public void setup() {
    this.windowTitle("Scratch for Java");
    this.windowResizable(false);
    this.imageMode(PConstants.CENTER);
    this.rectMode(PConstants.CENTER);
    this.background(0x222222);

    this.loading = this.loadSplashLogo();
    // Fit the picture into rather over half the window, and never enlarge it. For
    // the built-in logo this comes out at the size it has always been drawn at;
    // for a picture of any other size it keeps the loading text below it on
    // screen.
    var scale = Math.min(1, Math.min(
        this.RENDER_WIDTH * 0.55 / this.loading.width,
        this.RENDER_HEIGHT * 0.55 / this.loading.height));
    this.loading.resize((int) (this.loading.width * scale), (int) (this.loading.height * scale));

    this.isSetup = true;
  }

  /**
   * Loads the picture for the loading screen: the one chosen with
   * {@link org.openpatch.scratch.Window#useSplashLogo}, or the built-in logo.
   * A picture that cannot be read falls back to the logo rather than stopping
   * the project before it has started.
   */
  private PImage loadSplashLogo() {
    String chosen = org.openpatch.scratch.Window.getSplashLogo();
    if (chosen != null) {
      PImage image = null;
      try {
        image = this.loadImage(getPath(chosen));
      } catch (Exception e) {
        // reported below
      }
      if (image != null && image.width > 0) {
        return image;
      }
      System.err.println("\n==============================================");
      System.err.println("WARNING: Could not load the splash logo!");
      System.err.println("==============================================");
      System.err.println("\nPath: " + chosen);
      System.err.println("\nPossible reasons:");
      System.err.println("  1. The file does not exist at this location");
      System.err.println("  2. The file path is incorrect (check spelling)");
      System.err.println("  3. It is not a PNG, JPG or GIF");
      System.err.println("\nThe Scratch for Java logo is being shown instead.");
      System.err.println("==============================================\n");
    }
    return this.loadImage("loading.png");
  }

  public boolean isSetup() {
    return this.isSetup;
  }

  private void setLoadingText(String type, final String path) {
    this.loadingText = "Loading " + type + ": ";
    if (path.length() > 40) {
      this.loadingText += "..." + path.substring(path.length() - 40);
    } else {
      this.loadingText += path + " ".repeat(40 - path.length());
    }
  }

  /** Loads assets from the specified directory. */
  public void loadAssets() {
    Font.loadFont(Text.getDefaultFont());
    if (this.assets != null) {
      try {
        this.loadingText = "Finding files...";
        var sr = ClassLoader.getSystemResource(this.assets);
        var p = Paths.get(this.assets);
        if (sr != null) {
          try {
            p = Path.of(sr.toURI());
          } catch (FileSystemNotFoundException e) {
            p = Paths.get(this.assets);
          }
        }

        var imageFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
            .map(f -> f.toString())
            .filter(f -> f.endsWith(".png") || f.endsWith(".jpg") || f.endsWith(".jpeg"))
            .collect(Collectors.toList());
        var soundFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
            .map(f -> f.toString())
            .filter(f -> f.endsWith(".mp3") || f.endsWith(".wav"))
            .collect(Collectors.toList());
        var fontFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
            .map(f -> f.toString())
            .filter(f -> f.endsWith(".ttf") || f.endsWith(".otf"))
            .collect(Collectors.toList());
        this.numberAssets += imageFiles.size();
        this.numberAssets += soundFiles.size();
        this.numberAssets += fontFiles.size();
        for (var file : imageFiles) {
          this.setLoadingText("Image", file);
          Image.loadImage(file);
          this.loadedAssets += 1;
        }
        for (var file : fontFiles) {
          this.setLoadingText("Font", file);
          Font.loadFont(file);
          this.loadedAssets += 1;
        }
        for (var file : soundFiles) {
          this.setLoadingText("Sound", file);
          Sound.loadSound(file);
          this.loadedAssets += 1;
        }
      } catch (IOException | URISyntaxException e) {
      }
    }
  }

  private float loadingStatus() {
    return this.numberAssets > 0 ? this.loadedAssets / (float) this.numberAssets : 1;
  }

  /**
   * Handles mouse events.
   *
   * @param e the MouseEvent object
   */
  public void mouseEvent(MouseEvent e) {
    mouseDown = e.getAction() == MouseEvent.PRESS;
    if (this.state == State.RUNNING && this.stage != null) {
      var hooks = this.hooksFor(this.stage);
      if (hooks != null) {
        hooks.mouseEvent(e);
      }
    }
  }

  public boolean isMouseDown() {
    return mouseDown;
  }

  /**
   * Handles keyboard events.
   *
   * @param e the KeyEvent object
   */
  public void keyEvent(KeyEvent e) {
    if (this.state == State.RUNNING && this.stage != null) {
      var hooks = this.hooksFor(this.stage);
      if (hooks != null) {
        hooks.keyEvent(e);
      }
    }
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.keyCodePressed.put(e.getKeyCode(), true);
        // F12 toggles debug mode
        if (e.getKeyCode() == KeyCode.F12.getCode()) {
          this.debug = !this.debug;
          if (this.debug) {
            System.out.println("\n[Debug Mode Enabled]");
            System.out.println("Press F12 again to disable");
          } else {
            System.out.println("[Debug Mode Disabled]\n");
          }
        }
        break;
      case KeyEvent.RELEASE:
        this.keyCodePressed.put(e.getKeyCode(), false);
        break;
    }
  }

  public AbstractMap<Integer, Boolean> getKeyCodePressed() {
    return keyCodePressed;
  }

  public void setTextureSampling(int mode) {
    ((PGraphicsOpenGL) g).textureSampling(mode);
  }

  private void drawLoading() {
    var now = millis();
    if (this.splashStart == 0) {
      this.splashStart = now;
    }

    // The fade out may only begin once the fade in has finished, so that a
    // project with nothing to load still shows the logo arriving and leaving
    // rather than blinking once.
    var ready = this.loadingStatus() >= 1 && this.stage != null;
    if (ready && this.splashFadeOutStart == 0 && now - this.splashStart >= SPLASH_FADE_MS) {
      this.splashFadeOutStart = now;
    }

    float alpha;
    if (this.splashFadeOutStart > 0) {
      alpha = PApplet.map(now - this.splashFadeOutStart, 0, SPLASH_FADE_MS, 255, 0);
      if (alpha <= 0) {
        this.state = State.RUNNING;
        return;
      }
      // Fade away over the stage that is about to take over. Only its draw is
      // called, never its pre, so the project is not already being played
      // underneath the logo.
      var hooks = this.hooksFor(this.stage);
      if (hooks != null) {
        hooks.draw(this.getGraphics());
      }
    } else {
      alpha = PApplet.map(now - this.splashStart, 0, SPLASH_FADE_MS, 0, 255);
      this.background(0x222222);
    }
    alpha = PApplet.constrain(alpha, 0, 255);

    this.push();
    this.translate(this.width / 2, this.height / 2);
    if (this.splashFadeOutStart > 0) {
      this.noStroke();
      this.fill(0x22, 0x22, 0x22, alpha);
      this.rect(0, 0, this.width, this.height);
    }
    this.tint(255, alpha);
    this.image(this.loading, 0, 0);
    this.noTint();
    this.textAlign(CENTER);
    this.stroke(0xf58219);
    this.fill(255, alpha);
    this.textFont(Font.getDefaultFont());
    this.textSize(14);
    this.text(this.loadingText, 0, this.loading.height / 2 + 20);
    this.text(round(this.loadingStatus() * 100) + "%", 0, this.loading.height / 2 + 40);
    this.pop();
  }

  private void drawTransitionOut() {
    var alpha = PApplet.lerp(0, 255, (lastMillis - transitionStart) / (float) transitionDuration / 2.0f);
    if (alpha < 255) {
      this.push();
      this.translate(this.width / 2, this.height / 2);
      this.fill(0, 0, 0, alpha);
      this.rect(0, 0, this.getWidth(), this.getHeight());
      this.pop();
    } else {
      this.state = State.TRANSITIONING_IN;
      this.stage = this.transitionToStage;
      this.transitionStart = lastMillis;
    }
  }

  private void drawTransitionIn() {
    var alpha = PApplet.lerp(255, 0, (lastMillis - transitionStart) / (float) transitionDuration / 2.0f);
    if (alpha > 0) {
      var hooks = this.hooksFor(this.stage);
      if (hooks != null) {
        hooks.pre();
        hooks.draw(this.getGraphics());
      }
      this.push();
      this.translate(this.width / 2, this.height / 2);
      this.fill(0, 0, 0, alpha);
      this.rect(0, 0, this.getWidth(), this.getHeight());
      this.pop();
    } else {
      this.state = State.RUNNING;
    }
  }

  public static String getPath(String path) {

    // Expand ~
    if (path.startsWith("~")) {
      path = System.getProperty("user.home") + path.substring(1);
    }

    // Absolute filesystem path
    Path fsPath = Path.of(path);
    if (fsPath.isAbsolute()) {
      return fsPath.toString();
    }

    // Try to load from classpath
    var resource = Thread.currentThread().getContextClassLoader().getResource(path);
    if (resource != null) {
      if ("file".equals(resource.getProtocol())) {
        try {
          return Path.of(resource.toURI()).toString();
        } catch (URISyntaxException e) {
          throw new RuntimeException("Invalid resource URI", e);
        }
      } else {
        // resource is inside a JAR or non-file location
        // -> copy to a temp file
        try (var in = resource.openStream()) {
          Path tempFile = Files.createTempFile("resource-", "-" + Path.of(path).getFileName());
          tempFile.toFile().deleteOnExit();
          Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
          return tempFile.toString();
        } catch (IOException e) {
          throw new RuntimeException("Failed to extract resource", e);
        }
      }
    }

    // Fallback: treat as relative filesystem path
    return fsPath.toString();
  }

  /** Draws the application window. */
  public void draw() {
    var currentMillis = millis();
    if (lastMillis == 0) {
      lastMillis = currentMillis;
    }
    deltaTime = (currentMillis - lastMillis) / 1000.0;
    lastMillis = currentMillis;

    switch (state) {
      case LOADING:
        this.drawLoading();
        break;
      case RUNNING:
        var hooks = this.hooksFor(this.stage);
        if (hooks != null) {
          hooks.pre();
          hooks.draw(this.getGraphics());
        }
        break;
      case TRANSITIONING_IN:
        this.drawTransitionIn();
        break;
      case TRANSITIONING_OUT:
        this.drawTransitionOut();
        break;
    }
  }
}
