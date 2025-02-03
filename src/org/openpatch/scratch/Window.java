package org.openpatch.scratch;

import org.openpatch.scratch.internal.*;

/**
 * The Window class represents a singleton window for the application. It
 * provides various
 * constructors to create a window with different configurations such as
 * dimensions, full screen
 * mode, and asset paths. The class ensures that only one instance of the window
 * can be created at
 * any time.
 *
 * <p>
 * The class also provides methods to interact with the window and the
 * application, such as
 * retrieving the window dimensions, setting the debug mode, managing stages,
 * and exiting the
 * application.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * Window window = new Window();
 * window.setDebug(true);
 * }</pre>
 *
 * @see Stage
 */
public class Window {

  /** The default color used for debugging purposes. */
  public static final int[] DEBUG_COLOR = { 255, 0, 0 };

  /**
   * 2: Point Sampling. 3: Linear. 4: Bilinear. 5: Trilinear.
   *
   * <p>
   * Point sampling: both magnification and minification filtering are set to
   * nearest. Linear
   * sampling: magnification filtering is nearest, minification set to linear
   * Bilinear sampling:
   * both magnification filtering is set to linear and minification either to
   * linear-mipmap-nearest
   * (linear interpolation is used within a mipmap, but not between different
   * mipmaps). Trilinear
   * sampling: magnification filtering set to linear, minification to
   * linear-mipmap-linear, which
   * offers the best mipmap quality since linear interpolation to compute the
   * value in each of two
   * maps and then interpolates linearly between these two values.
   */
  public static int TEXTURE_SAMPLING_MODE = 4;

  private static Window instance;

  /**
   * Constructs a new Window with default dimensions. The default width is 480
   * pixels and the
   * default height is 360 pixels.
   */
  public Window() {
    this(480, 360);
  }

  /**
   * Constructs a new Window with the specified assets. The window will have a
   * default width of 480
   * pixels and a height of 360 pixels.
   *
   * @param assets the path to the assets to be used in the window
   */
  public Window(String assets) {
    this(480, 360, assets);
  }

  /**
   * Constructs a new Window with the specified width and height.
   *
   * @param width  the width of the window
   * @param height the height of the window
   */
  public Window(int width, int height) {
    this(width, height, null);
  }

  /**
   * Constructs a new Window with the specified width, height, and assets path.
   * Ensures that only
   * one instance of Window can be created.
   *
   * @param width  the width of the window
   * @param height the height of the window
   * @param assets the path to the assets
   * @throws Error if an instance of Window already exists
   */
  public Window(int width, int height, String assets) {
    super();
    if (Window.instance != null) {
      throw new Error("You can only have one Window.");
    }

    Window.instance = this;
    new Applet(width, height, false, assets);
  }

  /**
   * Constructs a new Window.
   *
   * @param fullScreen a boolean indicating whether the window should be in full
   *                   screen mode.
   */
  public Window(boolean fullScreen) {
    this(fullScreen, null);
  }

  /**
   * Constructs a new Window instance. If an instance of Window already exists, an
   * Error is thrown
   * to ensure only one Window instance is created.
   *
   * @param fullScreen a boolean indicating whether the window should be in full
   *                   screen mode
   * @param assets     a String specifying the path to the assets
   * @throws Error if an instance of Window already exists
   */
  public Window(boolean fullScreen, String assets) {
    this(fullScreen, 0, 0, assets);
  }

  public Window(boolean fullScreen, int width, int height, String assets) {
    if (Window.instance != null) {
      throw new Error("You can only have one Window.");
    }
    Window.instance = this;
    var applet = new Applet(width, height, fullScreen, assets);
    applet.setTextureSampling(TEXTURE_SAMPLING_MODE);
  }

  public Window(boolean fullScreen, int width, int height) {
    this(fullScreen, width, height, null);
  }

  /**
   * Returns the singleton instance of the Window class.
   *
   * @return the singleton instance of Window
   */
  public static Window getInstance() {
    return instance;
  }

  /**
   * Gets the seconds passed since the last frame.
   *
   * @return seconds since last frame
   */
  public double getDeltaTime() {
    return Applet.getInstance().getDeltaTime();
  }

  /**
   * Checks if the application is in debug mode.
   *
   * @return true if the application is in debug mode, false otherwise.
   */
  public boolean isDebug() {
    return Applet.getInstance().isDebug();
  }

  /**
   * Enables or disables the debug mode for the application.
   *
   * @param debug a boolean value where {@code true} enables debug mode and
   *              {@code false} disables
   *              it.
   */
  public void setDebug(boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  /**
   * Retrieves the width of the current window.
   *
   * @return the width of the window in pixels
   */
  public int getWidth() {
    return Applet.getInstance().getRenderWidth();
  }

  /**
   * Retrieves the height of the current window.
   *
   * @return the height of the window in pixels
   */
  public int getHeight() {
    return Applet.getInstance().getRenderHeight();
  }

  /**
   * Sets the current stage of the application.
   *
   * @param stage the new stage to be set
   */
  public void setStage(Stage stage) {
    Applet.getInstance().setStage(stage);
  }

  /**
   * Transitions to a new stage with a specified duration.
   *
   * @param stage    the new stage to transition to
   * @param duration the duration of the transition in milliseconds
   */
  public void transitionToStage(Stage stage, int duration) {
    Applet.getInstance().transitionToStage(stage, duration);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name  Name of the stage
   * @param stage A stage object
   */
  @Deprecated(since = "4.0.0")
  public void addStage(String name, Stage stage) {
    Applet.getInstance().addStage(name, stage);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void switchStage(String name) {
    Applet.getInstance().switchStage(name);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void removeStage(String name) {
    Applet.getInstance().removeStage(name);
  }

  /**
   * Retrieves the current stage from the Applet instance.
   *
   * @return the current Stage object
   */
  public Stage getStage() {
    return Applet.getInstance().getStage();
  }

  /**
   * Exits the application by invoking the `whenExits` method and then calling the
   * `exit` method on
   * the `Applet` instance.
   */
  public void exit() {
    this.whenExits();
    Applet.getInstance().exit();
  }

  /**
   * This method is called when the window exits. Override this method to define
   * custom behavior
   * when the window is closed.
   */
  public void whenExits() {
  }
}
