package org.openpatch.scratch.desktop;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Locale;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.openpatch.scratch.Launcher;
import org.openpatch.scratch.internal.platform.Display;

/**
 * A GLFW window with an OpenGL 3.3 core context. The window thread is the process's first thread
 * under {@link Launcher} (macOS: {@code -XstartOnFirstThread}); in a plain {@code java} run it is
 * a thread of the library's, with {@code glfw_async} on macOS.
 */
final class GlfwDisplay implements Display {

  private volatile long handle;
  private boolean initialized;
  private int[] windowedBounds;

  @Override
  public void start(Runnable windowLoop) {
    if (Launcher.isActive()) {
      Launcher.runOnFirstThread(windowLoop);
      return;
    }
    if (Platform.get() == Platform.MACOSX) {
      // GLFW calls are dispatched to the first thread's run loop (LWJGL's glfw_async)
      Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
    }
    Thread thread = new Thread(windowLoop, "scratch4j-window");
    thread.start(); // not a daemon: it keeps the program alive after main returns
  }

  private void init() {
    if (this.initialized) {
      return;
    }
    try {
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
    } catch (Throwable t) {
      throw failed(t);
    }
    this.initialized = true;
  }

  /** What went wrong, for the console and a released game's error box. */
  private static IllegalStateException failed(Throwable t) {
    if (String.valueOf(t.getMessage()).contains("Failed to locate library")) {
      // not the graphics card: LWJGL's native libraries are missing for this platform
      System.err.println("LWJGL's native libraries for " + System.getProperty("os.name") + " "
          + System.getProperty("os.arch") + " are missing. Use the scratch-<version>-all.jar, or put the "
          + "lwjgl-*-natives-<platform>.jar files next to the others (on the module path also add them with "
          + "--add-modules org.lwjgl.natives,org.lwjgl.glfw.natives,…).");
      return new IllegalStateException("Files it needs for this computer are missing.", t);
    }
    System.err.println("It needs OpenGL 3.3 (4.1 on macOS). Update the graphics driver, or run on another "
        + "computer.");
    return new IllegalStateException("It needs OpenGL 3.3 (4.1 on macOS). Updating the graphics driver may help.", t);
  }

  @Override
  public void open(int width, int height, String title, boolean visible, Input input) {
    this.init();
    try {
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
      glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
      glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
      glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
      // fullscreen is meant borderless: Alt+Tab or a click on another monitor must not minimise it
      glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE);
      glfwWindowHint(GLFW_VISIBLE, visible ? GLFW_TRUE : GLFW_FALSE);
      long window = glfwCreateWindow(width, height, title, 0, 0);
      if (window == 0) {
        throw new IllegalStateException("no OpenGL 3.3 core context");
      }
      this.handle = window;
      glfwMakeContextCurrent(window);
      GL.createCapabilities();
    } catch (Throwable t) {
      throw failed(t);
    }
    long window = this.handle;
    glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> input.key(key, scancode, action, mods));
    glfwSetCharCallback(window, (win, codepoint) -> input.typed(codepoint));
    glfwSetCursorPosCallback(window, (win, x, y) -> input.pointer(x, y));
    glfwSetMouseButtonCallback(window, (win, button, action, mods) -> input.button(button, action, mods));
    glfwSetScrollCallback(window, (win, dx, dy) -> input.scroll(dx, dy));
  }

  private GLFWVidMode mode() {
    this.init();
    return glfwGetVideoMode(glfwGetPrimaryMonitor());
  }

  @Override
  public int[] screenSize() {
    GLFWVidMode mode = this.mode();
    return mode == null ? null : new int[] {mode.width(), mode.height()};
  }

  @Override
  public int refreshRate() {
    GLFWVidMode mode = this.mode();
    return mode == null ? 0 : mode.refreshRate();
  }

  @Override
  public boolean vsyncNeedsFocus() {
    // Wayland may block glfwSwapBuffers for hidden windows
    return glfwGetPlatform() == GLFW_PLATFORM_WAYLAND;
  }

  @Override
  public boolean isFocused() {
    return glfwGetWindowAttrib(this.handle, GLFW_FOCUSED) == GLFW_TRUE;
  }

  @Override
  public void setSwapInterval(int interval) {
    glfwSwapInterval(interval);
  }

  @Override
  public void pollEvents() {
    glfwPollEvents();
  }

  @Override
  public void swapBuffers() {
    glfwSwapBuffers(this.handle);
  }

  @Override
  public void waitUntil(long nanoTime) {
    while (System.nanoTime() < nanoTime - 1_500_000) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
    while (System.nanoTime() < nanoTime) {
      Thread.onSpinWait();
    }
  }

  @Override
  public boolean shouldClose() {
    return glfwWindowShouldClose(this.handle);
  }

  @Override
  public void requestClose() {
    long h = this.handle;
    if (h != 0) {
      glfwSetWindowShouldClose(h, true);
    }
  }

  @Override
  public int[] windowSize() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      var w = stack.mallocInt(1);
      var h = stack.mallocInt(1);
      glfwGetWindowSize(this.handle, w, h);
      return new int[] {w.get(0), h.get(0)};
    }
  }

  @Override
  public int[] framebufferSize() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      var w = stack.mallocInt(1);
      var h = stack.mallocInt(1);
      glfwGetFramebufferSize(this.handle, w, h);
      return new int[] {w.get(0), h.get(0)};
    }
  }

  @Override
  public boolean setFullScreen(boolean fullScreen) {
    long monitor = glfwGetPrimaryMonitor();
    GLFWVidMode mode = glfwGetVideoMode(monitor);
    if (mode == null) {
      return false;
    }
    try (MemoryStack stack = MemoryStack.stackPush()) {
      if (fullScreen) {
        var x = stack.mallocInt(1);
        var y = stack.mallocInt(1);
        var w = stack.mallocInt(1);
        var h = stack.mallocInt(1);
        glfwGetWindowPos(this.handle, x, y);
        glfwGetWindowSize(this.handle, w, h);
        this.windowedBounds = new int[] {x.get(0), y.get(0), w.get(0), h.get(0)};
        glfwSetWindowMonitor(this.handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
      } else {
        int[] size = this.windowSize();
        int[] b = this.windowedBounds != null ? this.windowedBounds : new int[] {100, 100, size[0], size[1]};
        glfwSetWindowMonitor(this.handle, 0, b[0], b[1], b[2], b[3], GLFW_DONT_CARE);
      }
    }
    return true;
  }

  @Override
  public long createCursor(int width, int height, int[] argb, int hotX, int hotY) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      java.nio.ByteBuffer rgba = stack.malloc(width * height * 4);
      for (int p : argb) {
        rgba.put((byte) (p >> 16)).put((byte) (p >> 8)).put((byte) p).put((byte) (p >>> 24));
      }
      rgba.flip();
      GLFWImage image = GLFWImage.malloc(stack);
      image.set(width, height, rgba);
      return glfwCreateCursor(image, hotX, hotY);
    }
  }

  @Override
  public void setCursor(long cursor) {
    glfwSetCursor(this.handle, cursor);
  }

  @Override
  public void destroyCursor(long cursor) {
    glfwDestroyCursor(cursor);
  }

  @Override
  public String keyName(int key, int scancode) {
    return glfwGetKeyName(key, scancode);
  }

  @Override
  public void hide() {
    if (this.handle != 0) {
      glfwHideWindow(this.handle);
    }
  }

  @Override
  public void destroy() {
    glfwDestroyWindow(this.handle);
    glfwTerminate();
  }

  @Override
  public String describe() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      var cx = stack.mallocFloat(1);
      var cy = stack.mallocFloat(1);
      int[] window = this.windowSize();
      int[] framebuffer = this.framebufferSize();
      glfwGetMonitorContentScale(glfwGetPrimaryMonitor(), cx, cy);
      GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      return String.format(Locale.ROOT, "window %dx%d, framebuffer %dx%d, monitor content scale %.2f%s",
          window[0], window[1], framebuffer[0], framebuffer[1], cx.get(0),
          mode == null ? "" : String.format(Locale.ROOT, ", mode %dx%d", mode.width(), mode.height()));
    }
  }
}
