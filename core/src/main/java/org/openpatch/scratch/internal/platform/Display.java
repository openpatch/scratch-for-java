package org.openpatch.scratch.internal.platform;

/**
 * The window: a desktop window (GLFW) or the page's canvas. Key codes, actions and modifier bits
 * are GLFW's ({@link InputCodes}) on every platform. Everything but {@link #start} and
 * {@link #requestClose} is called on the window thread.
 */
public interface Display {

  /** Input as it arrives, on the window thread during {@link #pollEvents()}. */
  interface Input {
    void key(int key, int scancode, int action, int mods);

    void typed(int codepoint);

    /** The pointer, in window units from the top left. */
    void pointer(double x, double y);

    void button(int button, int action, int mods);

    /** Wheel steps; positive {@code dy} away from the user, as GLFW counts. */
    void scroll(double dx, double dy);
  }

  /** Runs the window loop on the thread the platform needs it on, and returns at once. */
  void start(Runnable windowLoop);

  /**
   * Opens a window of {@code width} × {@code height} screen units with its GL context current on
   * this thread; with {@code visible} false it stays hidden (Studio's preview).
   *
   * @throws IllegalStateException with what is missing when there is no window or no context
   */
  void open(int width, int height, String title, boolean visible, Input input);

  /** The screen's size in screen units, or null when unknown. */
  int[] screenSize();

  /** The screen's refresh rate, 0 when unknown. */
  int refreshRate();

  /** Whether vsync may only be on while the window has the focus (Wayland blocks hidden swaps). */
  boolean vsyncNeedsFocus();

  boolean isFocused();

  /** 1 waits for the screen when swapping, 0 does not. */
  void setSwapInterval(int interval);

  void pollEvents();

  /** Shows what was drawn; may wait for the screen. */
  void swapBuffers();

  /** Waits until {@link System#nanoTime()} reaches {@code nanoTime}, as precisely as it can. */
  void waitUntil(long nanoTime);

  boolean shouldClose();

  /** Makes {@link #shouldClose()} true. Any thread. */
  void requestClose();

  /** The window in screen units. */
  int[] windowSize();

  /** The window in pixels: what the renderer draws into. */
  int[] framebufferSize();

  /**
   * Borderless fullscreen on the main screen, or back to the window it was.
   *
   * @return whether it switched (there may be no screen to fill, or no user gesture in a browser)
   */
  boolean setFullScreen(boolean fullScreen);

  /** A mouse pointer from straight ARGB pixels; 0 when the platform has none. */
  long createCursor(int width, int height, int[] argb, int hotX, int hotY);

  /** Shows {@code cursor}, or the system's for 0. */
  void setCursor(long cursor);

  void destroyCursor(long cursor);

  /** The keyboard layout's name for a letter or digit key, lower case, or null. */
  String keyName(int key, int scancode);

  /** Out of the way of a message box. */
  void hide();

  /** Closes the window and lets go of the platform's window system. */
  void destroy();

  /** Window, framebuffer and screen geometry for {@code -Dscratch4j.debugGl}. */
  String describe();
}
