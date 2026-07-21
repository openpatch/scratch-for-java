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
 * 
 * @index-in-docs 3
 */
public class Window {

  /**
   * The default color used for debugging purposes.
   * You can modify this array to change the debug color.
   * 
   * <p>Debug mode can be toggled at runtime by pressing F12.
   * 
   * <p>For example, to set the debug color to green, you can do:
   * 
   * <pre>{@code
   * Window.DEBUG_COLOR = new int[] { 0, 255, 0 };
   * }</pre>
   */
  public static final int[] DEBUG_COLOR = { 255, 0, 0 };

  private static TextureSampling textureSampling = TextureSampling.BILINEAR;
  private static boolean fullScreen = false;

  /**
   * Makes the window fill the whole screen.
   *
   * <p>
   * Call it before the first stage is created, because the size of the window is
   * settled the moment it opens:
   *
   * <pre>{@code
   * public static void main(String[] args) {
   *   Window.useFullScreen();
   *   new MyStage();
   * }
   * }</pre>
   */
  public static void useFullScreen() {
    if (instance != null) {
      warnTooLate("useFullScreen()", "Window.useFullScreen();");
      return;
    }
    fullScreen = true;
  }

  /**
   * Chooses how pictures are smoothed when they are drawn larger or smaller than
   * they really are. Pixel art usually wants {@link TextureSampling#POINT}.
   *
   * <p>
   * Call it before the first stage is created.
   *
   * <pre>{@code
   * Window.useTextureSampling(TextureSampling.POINT);
   * new MyStage();
   * }</pre>
   *
   * @param sampling how to smooth pictures
   */
  public static void useTextureSampling(TextureSampling sampling) {
    if (sampling == null) {
      return;
    }
    if (instance != null) {
      warnTooLate("useTextureSampling()", "Window.useTextureSampling(TextureSampling.POINT);");
      return;
    }
    textureSampling = sampling;
  }

  /**
   * Returns how pictures are being smoothed.
   *
   * @return the current setting
   */
  public static TextureSampling getTextureSampling() {
    return textureSampling;
  }

  /** Says so, loudly, when a window setting is chosen after the window exists. */
  private static void warnTooLate(String what, String example) {
    System.err.println("\n==============================================");
    System.err.println("WARNING: " + what + " was called too late!");
    System.err.println("==============================================");
    System.err.println("\nThe window already exists, so this had no effect.");
    System.err.println("\nTip: Call it before the first stage is created:");
    System.err.println("       " + example);
    System.err.println("       new MyStage();");
    System.err.println("==============================================\n");
  }

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
    this(fullScreen, width, height, assets);
  }



  private Window(boolean fullScreen, int width, int height, String assets) {
    if (Window.instance != null) {
      System.err.println("\n==============================================");
      System.err.println("ERROR: Cannot create multiple Windows!");
      System.err.println("==============================================");
      System.err.println("\nA Window instance already exists.");
      System.err.println("\nPossible reasons:");
      System.err.println("  1. You created 'new Window()' multiple times");
      System.err.println("  2. Your code instantiates Window in multiple places");
      System.err.println("\nTip: Scratch for Java only supports one Window.");
      System.err.println("     Remove duplicate Window creation code.");
      System.err.println("==============================================\n");
      throw new Error("You can only have one Window.");
    }
    Window.instance = this;
    var applet = new Applet(width, height, fullScreen, assets);
    applet.setTextureSampling(textureSampling.getMode());
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
   * <p>Debug mode can be toggled at runtime by pressing F12.
   *
   * @return true if the application is in debug mode, false otherwise.
   */
  public boolean isDebug() {
    return Applet.getInstance().isDebug();
  }

  /**
   * Enables or disables the debug mode for the application.
   * 
   * <p>Debug mode shows sprite positions, hitboxes, and other debug information.
   * It can also be toggled at runtime by pressing F12.
   *
   * @param debug a boolean value where {@code true} enables debug mode and
   *              {@code false} disables
   *              it.
   */
  public void setDebug(boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  /**
   * Prints a debug message to stdout when debug mode is enabled.
   * The message is prefixed with the window's class name.
   *
   * <p>Example:
   * <pre>{@code
   * this.debug("fps =", getFps());
   * // prints: [MyWindow] fps = 60
   * }</pre>
   *
   * @param values one or more values to print
   */
  public void debug(Object... values) {
    if (!Applet.getInstance().isDebug()) return;
    StringBuilder sb = new StringBuilder("[").append(getClass().getSimpleName()).append("]");
    for (Object v : values) {
      sb.append(" ").append(v);
    }
    System.out.println(sb);
  }


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
   *
   * @example.preview WindowSetStage.gif
   * @example.files WindowSetStage.java
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

  /**
   * Returns the version of the library.
   *
   * @return the version of the library as a String
   */
  public String getLibraryVersion() {
    return this.getClass().getPackage().getImplementationVersion();
  }

  /**
   * Returns the title of the library.
   *
   * @return the title of the library as a String
   */
  public String getLibraryTitle() {
    return this.getClass().getPackage().getImplementationTitle();
  }
}
