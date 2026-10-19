package org.openpatch.scratch.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
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
    CREATING_STAGE,
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
  private volatile boolean assetsListed;
  private PImage loading;
  /** How long the loading screen takes to fade in, and again to fade out. */
  private static final int SPLASH_FADE_MS = 300;
  private int splashStart;
  private int splashFadeOutStart;
  private final String assets;
  private Stage stage;
  /**
   * How to reach a stage's render loop. Hooks capture their owning Stage, so
   * values must be weak as well as keys. Stage itself keeps its hooks alive.
   */
  private final Map<Stage, WeakReference<StageHooks>> stageHooks =
      java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
  private int transitionStart;
  private int transitionDuration;
  private Stage transitionToStage;
  private Supplier<? extends Stage> transitionStageFactory;
  private int lastMillis;
  private double deltaTime;

  /** Game time: paused, slowed down or sped up (see {@link GameClock}). */
  private final GameClock clock = new GameClock();
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
    // a preview is not shown, so it needs no screen of its own
    this.FULLSCREEN = fullscreen && !Preview.isOn();
    this.assets = assets;
    if (Preview.isOn()) {
      Preview.startTimeout();
    }

    this.registerMethod("mouseEvent", this);
    this.registerMethod("keyEvent", this);
    if (Applet.instance == null) {
      Applet.instance = this;
    }
    // On Windows with display scaling, AWT reports the screen in scaled pixels
    // (1280x720 for 1920x1080 at 150%), while JOGL places its window in real
    // ones: fullScreen() then covers only part of the screen and windows are
    // centred off it. Processing turns AWT's scaling off itself, but only when
    // it finds fenster.exe, which ships with the Processing IDE and not with
    // this library. It must happen before anything starts AWT.
    if (PApplet.platform == PConstants.WINDOWS && System.getProperty("sun.java2d.uiScale") == null) {
      System.setProperty("sun.java2d.uiScale", "1");
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
   * The stage as it is in the window, without the bars around it and at the
   * stage's own size. The window can be bigger than the stage (maximized, a
   * high-resolution screen, a desktop that sizes windows itself), and then the
   * stage is letterboxed into it. Call it on the render thread, after drawing.
   *
   * @return the stage's picture, {@link #getRenderWidth()} by
   *         {@link #getRenderHeight()}
   */
  public PImage getStageImage() {
    float ratio = Math.min((float) this.width / this.RENDER_WIDTH,
        (float) this.height / this.RENDER_HEIGHT);
    int width = Math.round(this.RENDER_WIDTH * ratio);
    int height = Math.round(this.RENDER_HEIGHT * ratio);
    var area = this.get((this.width - width) / 2, (this.height - height) / 2, width, height);
    if (width != this.RENDER_WIDTH || height != this.RENDER_HEIGHT) {
      area.resize(this.RENDER_WIDTH, this.RENDER_HEIGHT);
    }
    return area;
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
    this.stageHooks.put(stage, new WeakReference<>(hooks));
  }

  private StageHooks hooksFor(Stage stage) {
    var hooks = stage == null ? null : this.stageHooks.get(stage);
    return hooks == null ? null : hooks.get();
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

  public void transitionToStage(Supplier<? extends Stage> stage, int duration) {
    Objects.requireNonNull(stage, "Stage supplier must not be null");
    if (this.state == State.RUNNING) {
      this.transitionStart = millis();
      this.transitionDuration = duration;
      this.transitionStageFactory = stage;
      this.state = State.TRANSITIONING_OUT;
    }
  }

  /** Whether a transition started by {@link #transitionToStage} is still fading. */
  public boolean isTransitioning() {
    return this.state == State.TRANSITIONING_OUT || this.state == State.CREATING_STAGE
        || this.state == State.TRANSITIONING_IN;
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
   * Returns the game time since the last game step in seconds.
   *
   * @return the time since the last step in seconds
   */
  public double getDeltaTime() {
    return deltaTime;
  }

  /** Milliseconds of game time since the program started. */
  public int getGameMillis() {
    return (int) this.clock.millis();
  }

  /** The game clock: pause, single steps and the game speed. */
  public GameClock getClock() {
    return this.clock;
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
    if (Preview.isOn()) {
      // out of sight: a hidden window would not be drawn at all
      this.surface.setLocation(-20000, -20000);
    }
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
        image = Image.loadImageOrBuiltin(chosen);
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

  /** The folder that is preloaded when the window is given none. */
  static final String DEFAULT_ASSETS = "assets";

  /**
   * Loads the assets folder: the one given to the window, or else a folder
   * named {@code assets}, if the project has one.
   *
   * <p>Every file is stored under the path a sprite would use for it, the
   * assets folder followed by the path inside it ({@code assets/cat.png}), so
   * that loading it later finds it already there.
   */
  public void loadAssets() {
    Font.loadFont(Text.getDefaultFont());
    var folder = this.assets != null ? this.assets : DEFAULT_ASSETS;
    var root = findAssetsFolder(folder);
    if (root == null) {
      if (this.assets != null) {
        reportMissingAssetsFolder(this.assets);
      }
      this.assetsListed = true;
      return;
    }
    try {
      this.loadingText = "Finding files...";
      var all = listAssets(folder, root);
      var imageFiles = all.stream()
          .filter(f -> hasExtension(f, ".png", ".jpg", ".jpeg"))
          .collect(Collectors.toList());
      var soundFiles = all.stream()
          .filter(f -> hasExtension(f, ".mp3", ".wav"))
          .collect(Collectors.toList());
      var fontFiles = all.stream()
          .filter(f -> hasExtension(f, ".ttf", ".otf"))
          .collect(Collectors.toList());
      this.numberAssets += imageFiles.size();
      this.numberAssets += soundFiles.size();
      this.numberAssets += fontFiles.size();
      this.assetsListed = true;
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
    } catch (IOException e) {
    } finally {
      // whatever went wrong, the project must not wait on its loading screen
      this.assetsListed = true;
      this.loadedAssets = this.numberAssets;
    }
  }

  /**
   * Finds an assets folder on the classpath or, relative to the working
   * directory, on disk.
   *
   * @param folder the folder as it was written
   * @return the folder, or null if there is none
   */
  static Path findAssetsFolder(String folder) {
    folder = folder.replaceFirst("^~", System.getProperty("user.home"));
    var onDisk = Paths.get(folder);
    if (!onDisk.isAbsolute()) {
      var resource = Thread.currentThread().getContextClassLoader().getResource(folder);
      if (resource != null) {
        try {
          var onClasspath = classpathFolder(resource.toURI());
          if (onClasspath != null && Files.isDirectory(onClasspath)) {
            return onClasspath;
          }
        } catch (URISyntaxException | IOException | RuntimeException e) {
        }
      }
      var inJar = folderInsideAJar(folder);
      if (inJar != null) {
        return inJar;
      }
    }
    return Files.isDirectory(onDisk) ? onDisk : null;
  }

  /**
   * Looks for a folder inside the jars on the classpath. A jar only has the
   * folder as a resource of its own when it was packed with an entry for it,
   * which not every tool writes, so each jar is opened and looked into.
   */
  private static Path folderInsideAJar(String folder) {
    try {
      var manifests = Thread.currentThread().getContextClassLoader()
          .getResources("META-INF/MANIFEST.MF");
      while (manifests.hasMoreElements()) {
        var manifest = manifests.nextElement().toString();
        if (!manifest.startsWith("jar:")) {
          continue;
        }
        var jar = URI.create(manifest.substring(0, manifest.indexOf("!/") + 2));
        FileSystem opened = null;
        FileSystem fs;
        try {
          fs = FileSystems.getFileSystem(jar);
        } catch (FileSystemNotFoundException e) {
          fs = opened = FileSystems.newFileSystem(jar, Map.of());
        }
        var candidate = fs.getPath(folder);
        if (Files.isDirectory(candidate)) {
          // left open, since the files are read from it after this returns
          return candidate;
        }
        if (opened != null) {
          opened.close();
        }
      }
    } catch (IOException | RuntimeException e) {
    }
    return null;
  }

  /**
   * Turns a folder on the classpath into a path, also when it is inside a jar:
   * a project that was packaged and shared keeps its assets in there.
   */
  private static Path classpathFolder(URI uri) throws IOException {
    if ("file".equals(uri.getScheme())) {
      return Path.of(uri);
    }
    if (!"jar".equals(uri.getScheme())) {
      return null;
    }
    try {
      return Path.of(uri);
    } catch (FileSystemNotFoundException e) {
      // left open, since the files are read from it after this returns
      FileSystems.newFileSystem(uri, Map.of());
      return Path.of(uri);
    }
  }

  /**
   * Notes a file that was loaded while the project was already running, so it
   * was not ready when it was first used and the game may have stuttered
   * while it loaded. Debug mode says which file, and how to load it in
   * advance.
   *
   * @param type the kind of file, e.g. "image"
   * @param path the path the file was loaded by
   */
  public static void noteLoadedLate(String type, String path) {
    var applet = instance;
    if (applet == null || !applet.debug || applet.state == State.LOADING) {
      return;
    }
    var folder = applet.assets != null ? applet.assets : DEFAULT_ASSETS;
    var prefix = folder.replace('\\', '/').replaceAll("/+$", "") + "/";
    var hint = path.replace('\\', '/').startsWith(prefix)
        ? "the folder '" + folder + "' could not be loaded in advance"
        : "put it into '" + folder + "' to load it while the loading screen shows";
    System.out.println("[Debug] Loaded " + type + " late: " + path + " (" + hint + ")");
  }

  /**
   * Lists the files in an assets folder by the paths a sprite would use for
   * them: the folder as it was written, followed by the path inside it.
   *
   * @param folder the folder as it was written
   * @param root   the folder as {@link #findAssetsFolder(String)} found it
   * @return the paths of the files
   * @throws IOException if the folder cannot be read
   */
  static List<String> listAssets(String folder, Path root) throws IOException {
    var prefix = folder.replace('\\', '/').replaceAll("/+$", "");
    try (var files = Files.walk(root)) {
      return files.filter(Files::isRegularFile)
          .map(f -> prefix + "/" + root.relativize(f).toString().replace('\\', '/'))
          .sorted()
          .collect(Collectors.toList());
    }
  }

  private static boolean hasExtension(String file, String... extensions) {
    var lower = file.toLowerCase();
    for (var extension : extensions) {
      if (lower.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }

  private static void reportMissingAssetsFolder(String folder) {
    System.err.println("\n==============================================");
    System.err.println("WARNING: Assets folder not found!");
    System.err.println("==============================================");
    System.err.println("You wrote:      " + folder);
    System.err.println("Java looked at: " + Paths.get(folder).toAbsolutePath());
    System.err.println("\nNothing is loaded in advance, so each image and sound");
    System.err.println("is loaded the first time it is used.");
    System.err.println("\nTip: Check the folder name for typos. A folder named");
    System.err.println("     'assets' is found without naming it at all:");
    System.err.println("       new Window(480, 360);");
    System.err.println("==============================================\n");
  }

  private float loadingStatus() {
    if (!this.assetsListed) {
      return 0;
    }
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
      if (this.transitionStageFactory == null) {
        this.state = State.TRANSITIONING_IN;
        this.stage = this.transitionToStage;
        this.transitionToStage = null;
        this.transitionStart = lastMillis;
      } else {
        // Paint black for one frame before a potentially slow constructor runs.
        this.push();
        this.translate(this.width / 2, this.height / 2);
        this.fill(0, 0, 0, 255);
        this.rect(0, 0, this.getWidth(), this.getHeight());
        this.pop();
        this.state = State.CREATING_STAGE;
      }
    }
  }

  private void createTransitionStage() {
    var factory = this.transitionStageFactory;
    this.transitionStageFactory = null;
    try {
      this.stage = Objects.requireNonNull(factory.get(), "Stage supplier returned null");
      this.state = State.TRANSITIONING_IN;
      this.transitionStart = millis();
    } catch (RuntimeException | Error failure) {
      this.state = State.RUNNING;
      throw failure;
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

  /** When the window first ran without a stage (0: it has one, or not yet). */
  private int withoutStageSince;
  private boolean missingStageExplained;

  /**
   * A window nobody put a stage into stays empty. Says what is missing once the
   * program had a moment to call setStage.
   */
  private void explainMissingStage(int now) {
    if (this.missingStageExplained) return;
    if (this.withoutStageSince == 0) {
      this.withoutStageSince = now;
      return;
    }
    if (now - this.withoutStageSince < 2000) return;
    this.missingStageExplained = true;
    System.err.println("\n==============================================");
    System.err.println("NOTE: The window has no stage to show");
    System.err.println("==============================================");
    System.err.println("\nPut a stage into the window, and it shows up:");
    System.err.println("       Window myWindow = new Window(480, 360);");
    System.err.println("       myWindow.setStage(new MyStage());");
    System.err.println("==============================================\n");
  }

  /** Draws the application window. */
  public void draw() {
    var currentMillis = millis();
    if (lastMillis == 0) {
      lastMillis = currentMillis;
    }
    double realSeconds = (currentMillis - lastMillis) / 1000.0;
    lastMillis = currentMillis;

    switch (state) {
      case LOADING:
        this.deltaTime = realSeconds;
        this.drawLoading();
        break;
      case RUNNING:
        var hooks = this.hooksFor(this.stage);
        if (hooks == null) {
          this.explainMissingStage(currentMillis);
        }
        if (hooks != null && Preview.isOn()) {
          var next = Preview.frame(this, this.stage, hooks, this.getGraphics());
          if (next != null) {
            this.setStage(next);
          }
          break;
        }
        if (hooks != null) {
          int steps = this.clock.frame(realSeconds);
          this.deltaTime = this.clock.stepSeconds();
          for (int i = 0; i < steps; i++) {
            this.clock.advance();
            hooks.pre();
          }
          hooks.draw(this.getGraphics());
          if (Preview.capturing()) {
            Preview.capture(this, this.stage, hooks, this.getGraphics(), steps);
          }
        }
        break;
      case TRANSITIONING_IN:
        this.drawTransitionIn();
        break;
      case TRANSITIONING_OUT:
        this.drawTransitionOut();
        break;
      case CREATING_STAGE:
        this.createTransitionStage();
        break;
    }
  }
}
