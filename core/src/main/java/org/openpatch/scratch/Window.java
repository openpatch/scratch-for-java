package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.Natives;
import org.openpatch.scratch.internal.window.WindowHost;

/**
 * The window of a program: there is one, created first, and it shows one stage at a time.
 *
 * <pre>{@code
 * public static void main(String[] args) {
 *   Window window = new Window(480, 360);
 *   window.setStage(new MyStage());
 * }
 * }</pre>
 *
 * <p>The constructor returns at once; the window runs on a thread of its own and the game ticks
 * 60 times a second of game time. {@code main} may carry on, e.g. with
 * {@code stage.waitSeconds(…)} between steps.
 */
public class Window {

  private static volatile Window instance;
  private static boolean fullScreen;
  private static boolean pixelArt;
  private static boolean smoothPixelArt;
  private static double frameSmoothing;
  private static String splashLogo;
  private static final List<Extension> registered = new ArrayList<>();

  private final Game game;
  private final WindowHost host;

  /** A window of 480 × 360, Scratch's stage size. */
  public Window() {
    this(480, 360, null);
  }

  /**
   * A window of 480 × 360 that loads every image, sound and font in a folder before the first
   * frame, showing a loading screen.
   *
   * @param assets the folder, e.g. {@code "assets"}
   */
  public Window(String assets) {
    this(480, 360, assets);
  }

  /**
   * A window whose stage has this size in stage pixels. The window opens at that size (in pixel
   * art at the largest whole multiple that fits the screen) and scales the stage to fit when it
   * is resized.
   *
   * @param width the stage width
   * @param height the stage height
   */
  public Window(int width, int height) {
    this(width, height, null);
  }

  /**
   * A window of a size that loads a folder of assets first.
   *
   * @param width the stage width
   * @param height the stage height
   * @param assets the folder, or null
   */
  public Window(int width, int height, String assets) {
    org.openpatch.scratch.internal.Release.start();
    Natives.init();
    synchronized (Window.class) {
      if (instance != null) {
        throw new IllegalStateException("There can only be one window. Use setStage to show another stage.");
      }
      instance = this;
    }
    this.game = Game.start(width, height);
    this.game.setPixelArt(pixelArt);
    this.game.getExtensions().install(
        org.openpatch.scratch.internal.Extensions.discover(List.copyOf(registered), Extension.coreVersion()));
    this.host = WindowHost.start(this.game, this, new WindowHost.Settings(this.getLibraryTitle(), fullScreen,
        pixelArt, smoothPixelArt, frameSmoothing, splashLogo, assets, List.copyOf(registered)));
  }

  /**
   * @return the window, or null before it was created
   *
   * @example.files WindowGetInstance.java
   */
  public static Window getInstance() {
    return instance;
  }

  // ---- settings before the window opens --------------------------------------------------

  /**
   * Opens the window full screen (a borderless window at the screen's size). Call it before {@code new Window}.
   *
   * @example.files WindowUseFullScreen.java
   */
  public static void useFullScreen() {
    fullScreen = true;
  }

  /**
   * Pixel-art mode: images are not smoothed, the stage scales by whole numbers only, the default
   * font is Jersey 10 and font sizes are rounded to its pixels. Call it before {@code new Window}.
   */
  public static void usePixelArt() {
    pixelArt = true;
  }

  /** @return whether pixel-art mode is on */
  public static boolean isPixelArt() {
    return pixelArt;
  }

  /**
   * Smooth pixel art (Godot's "canvas items" way of showing pixel art): as {@link #usePixelArt()},
   * but the camera moves without snapping and zoom works at any factor. The world is drawn at the
   * window's resolution with the sprites still sampled by nearest neighbour, so every pixel stays
   * a sharp square while things move — the grid wobbles by a screen pixel instead of the picture
   * stepping whole stage pixels (the look of Hyper Light Drifter, as Godot's guide puts it). With
   * it, scrolling does not need {@link #useFrameSmoothing(double)}. Call it before
   * {@code new Window}.
   */
  public static void useSmoothPixelArt() {
    pixelArt = true;
    smoothPixelArt = true;
  }

  /** @return whether the pixel art scrolls smoothly (see {@link #useSmoothPixelArt()}) */
  public static boolean isSmoothPixelArt() {
    return smoothPixelArt;
  }

  /**
   * Frame smoothing: each frame is presented mixed with the one before it, this much in percent.
   * A screen that holds each frame until the next (every 60 Hz LCD) smears whatever moves,
   * because the eye gets one picture per 16.7 ms; two pictures per frame (50 is a good value)
   * look like one picture per 8.3 ms and smear about half as much. The world keeps its pixels:
   * the moment nothing moves, current and previous frame are the same and the picture is fully
   * sharp. While things move, edges are softer — that is the trade. Call it before
   * {@code new Window}; 0 (the default) turns it off.
   *
   * @param percent how much of the previous frame each presented one keeps, 0 to 100
   */
  public static void useFrameSmoothing(double percent) {
    frameSmoothing = Math.max(0, Math.min(100, percent));
  }

  /** @return the frame smoothing in percent, 0 when off */
  public static double getFrameSmoothing() {
    return frameSmoothing;
  }

  /**
   * The logo on the loading screen. Call it before {@code new Window}.
   *
   * @param path an image
   *
   * @example.files WindowUseSplashLogo.java
   */
  public static void useSplashLogo(String path) {
    splashLogo = path;
  }

  /**
   * Uses an extension that does not register itself through {@code META-INF/services} (one in
   * your own project). Call it before {@code new Window}.
   *
   * @param extension the extension
   */
  public static void use(Extension extension) {
    registered.add(extension);
  }

  // ---- the game -------------------------------------------------------------------------

  /**
   * Pauses the game: no ticks run; keys and the mouse still reach the stage and sprites.
   *
   * @example.files WindowPause.java
   */
  public void pause() {
    this.game.setPaused(true);
  }

  /**
   * Goes on after {@link #pause()}.
   *
   * @example.files WindowPause.java
   */
  public void resume() {
    this.game.setPaused(false);
  }

  /**
   * @return whether the game is paused
   *
   * @example.files WindowPause.java
   */
  public boolean isPaused() {
    return this.game.isPaused();
  }

  /**
   * While paused: runs exactly one tick.
   *
   * @example.files WindowStep.java
   */
  public void step() {
    this.game.step();
  }

  /**
   * How fast the game runs: 1 is normal, 2 twice as fast, 0.5 half as fast; up to 8. Each tick
   * still is 1/60 s of game time; there are more or fewer of them per second.
   *
   * @param speed the game speed
   *
   * @example.files WindowSetGameSpeed.java
   */
  public void setGameSpeed(double speed) {
    this.game.setGameSpeed(speed);
  }

  /**
   * @return the game speed
   *
   * @example.files WindowSetGameSpeed.java
   */
  public double getGameSpeed() {
    return this.game.getGameSpeed();
  }

  /**
   * @return whether debug mode is on
   *
   * @example.files WindowIsDebug.java
   */
  public boolean isDebug() {
    return this.game.isDebug();
  }

  /**
   * Debug mode shows what its panel in the bottom right corner has ticked: hitboxes, positions,
   * the mouse and camera, touching pairs, hidden sprites, the clones and the frame rate, and it
   * lets {@code debug(…)} print. F12 switches it too, and {@code -Dscratch4j.debug=…} presets
   * which options are on.
   *
   * @param debug on or off
   *
   * @example.files WindowSetDebug.java
   */
  public void setDebug(boolean debug) {
    this.game.setDebug(debug);
  }

  /**
   * Prints values, but only in debug mode.
   *
   * @param values what to print
   *
   * @example.files WindowDebug.java
   */
  public void debug(Object... values) {
    if (this.game.isDebug()) {
      StringBuilder line = new StringBuilder("Window:");
      for (Object v : values) {
        line.append(' ').append(v);
      }
      System.out.println(line);
    }
  }

  /**
   * @return the stage width in stage pixels
   *
   * @example.files WindowGetWidth.java
   */
  public int getWidth() {
    return this.game.getWidth();
  }

  /**
   * @return the stage height in stage pixels
   *
   * @example.files WindowGetHeight.java
   */
  public int getHeight() {
    return this.game.getHeight();
  }

  // ---- stages -----------------------------------------------------------------------------

  /**
   * Shows a stage from the next tick on.
   *
   * @param stage the stage
   *
   * @example.files WindowSetStage.java
   */
  public void setStage(Stage stage) {
    if (this.game.isGameThread()) {
      this.game.setStage(stage);
    } else {
      this.game.runOnGameThread(() -> this.game.setStage(stage));
    }
  }

  /**
   * @return the stage shown
   *
   * @example.files WindowGetStage.java
   */
  public Stage getStage() {
    return this.game.getStage();
  }

  /**
   * Fades to a stage through black.
   *
   * @param stage the stage
   * @param seconds how long
   *
   * @example.files WindowTransitionToStage.java
   */
  public void transitionToStage(Stage stage, double seconds) {
    this.transitionToStage(() -> stage, Transition.FADE, seconds);
  }

  /**
   * Fades to a stage made now, on the game thread: a slow constructor shows the loading screen
   * instead of freezing the window.
   *
   * @param stage makes the stage, e.g. {@code () -> new Level2()}
   * @param seconds how long
   *
   * @example.files WindowTransitionToStage.java
   */
  public void transitionToStage(Supplier<? extends Stage> stage, double seconds) {
    this.transitionToStage(stage, Transition.FADE, seconds);
  }

  /**
   * Shows a stage made now through a transition.
   *
   * <pre>{@code
   * window.transitionToStage(() -> new Level2(), Transition.SLIDE_LEFT, 0.5);
   * }</pre>
   *
   * @param stage makes the stage
   * @param transition how
   * @param seconds how long
   *
   * @example.files WindowTransitionToStage.java
   */
  public void transitionToStage(Supplier<? extends Stage> stage, Transition transition, double seconds) {
    this.game.transition(stage, transition.getName(), transition.getSource(), seconds);
  }

  /** @return whether a transition is in progress */
  public boolean isTransitioning() {
    return this.game.isTransitioning();
  }

  /**
   * Ends the program: {@link #whenExits()} runs first.
   *
   * @example.files WindowExit.java
   */
  public void exit() {
    this.host.close();
  }

  /**
   * Called when the program ends, on the game thread. Override it to save, say.
   *
   * @example.files WindowWhenExits.java
   */
  public void whenExits() {}

  /**
   * @return the version of Scratch for Java, e.g. "7.0.0"
   *
   * @example.files WindowGetLibraryVersion.java
   */
  public String getLibraryVersion() {
    return Extension.coreVersion();
  }

  /**
   * @return "Scratch for Java"
   *
   * @example.files WindowGetLibraryTitle.java
   */
  public String getLibraryTitle() {
    return "Scratch for Java";
  }
}
