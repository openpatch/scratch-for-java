package org.openpatch.scratch.web;

import org.openpatch.scratch.internal.platform.Display;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int32Array;

/**
 * The page's canvas as the window. DOM events wait in s4j.js until {@link #pollEvents()} hands
 * them to the input, so they arrive on the window thread as on the desktop; a swap waits for the
 * browser's next animation frame, which is what lets the page draw and the other threads run.
 */
final class CanvasDisplay implements Display {

  private Input input;

  @Override
  public void start(Runnable windowLoop) {
    new Thread(windowLoop, "scratch4j-window").start();
  }

  @Override
  public void open(int width, int height, String title, boolean visible, Input input) {
    if (!createWindow(title)) {
      throw new IllegalStateException("This browser cannot draw the game: it has no WebGL2.");
    }
    this.input = input;
  }

  @Override
  public int[] screenSize() {
    return new int[] {screenWidth(), screenHeight()};
  }

  @Override
  public int refreshRate() {
    return 0; // the browser paces animation frames to the screen; 0 counts as 60 Hz
  }

  @Override
  public boolean vsyncNeedsFocus() {
    return false;
  }

  @Override
  public boolean isFocused() {
    return focused();
  }

  @Override
  public void setSwapInterval(int interval) {
    // animation frames always wait for the screen
  }

  @Override
  public void pollEvents() {
    pollDom();
    for (int type; (type = nextEvent()) != 0; ) {
      switch (type) {
        case 1 -> this.input.key((int) event(1), (int) event(2), (int) event(3), (int) event(4));
        case 2 -> this.input.typed((int) event(1));
        case 3 -> this.input.pointer(event(1), event(2));
        case 4 -> this.input.button((int) event(1), (int) event(2), (int) event(3));
        case 5 -> this.input.scroll(event(1), event(2));
        default -> {}
      }
    }
  }

  @Override
  public void swapBuffers() {
    nextFrame();
  }

  @Override
  public void waitUntil(long nanoTime) {
    long millis = (nanoTime - System.nanoTime()) / 1_000_000;
    if (millis > 0) {
      try {
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public boolean shouldClose() {
    return closing();
  }

  @Override
  public void requestClose() {
    setClosing();
  }

  @Override
  public int[] windowSize() {
    return new int[] {windowWidth(), windowHeight()};
  }

  @Override
  public int[] framebufferSize() {
    return new int[] {framebufferWidth(), framebufferHeight()};
  }

  @Override
  public boolean setFullScreen(boolean fullScreen) {
    return requestFullScreen(fullScreen);
  }

  @Override
  public long createCursor(int width, int height, int[] argb, int hotX, int hotY) {
    return createCursor(width, height, Int32Array.copyFromJavaArray(argb), hotX, hotY);
  }

  @Override
  public void setCursor(long cursor) {
    setCursor((int) cursor);
  }

  @Override
  public void destroyCursor(long cursor) {
    destroyCursor((int) cursor);
  }

  @Override
  public String keyName(int key, int scancode) {
    return keyName(key);
  }

  @Override
  public void hide() {
    // the page stays; the message shows over it
  }

  @Override
  public void destroy() {
    // the canvas belongs to the page
  }

  @Override
  public String describe() {
    int[] window = this.windowSize(), framebuffer = this.framebufferSize();
    return "canvas " + window[0] + "x" + window[1] + ", framebuffer " + framebuffer[0] + "x" + framebuffer[1];
  }

  // ---- s4j.js -------------------------------------------------------------------------------

  @Async
  private static native Void nextFrame();

  private static void nextFrame(AsyncCallback<Void> callback) {
    frame(() -> callback.complete(null));
  }

  @JSFunctor
  interface Done extends JSObject {
    void done();
  }

  @JSBody(params = "done", script = "S4J.glfw.frame(done);")
  private static native void frame(Done done);

  @JSBody(params = "title", script = "return S4J.glfw.createWindow(title) === 1;")
  private static native boolean createWindow(String title);

  @JSBody(script = "S4J.glfw.pollEvents();")
  private static native void pollDom();

  @JSBody(script = "return S4J.glfw.nextEvent();")
  private static native int nextEvent();

  @JSBody(params = "i", script = "return S4J.glfw.event(i);")
  private static native double event(int i);

  @JSBody(params = "key", script = "return S4J.glfw.keyName(key);")
  private static native String keyName(int key);

  @JSBody(script = "return S4J.glfw.focused();")
  private static native boolean focused();

  @JSBody(script = "return S4J.glfw.shouldClose();")
  private static native boolean closing();

  @JSBody(script = "S4J.glfw.setShouldClose(true);")
  private static native void setClosing();

  @JSBody(script = "return S4J.glfw.windowWidth();")
  private static native int windowWidth();

  @JSBody(script = "return S4J.glfw.windowHeight();")
  private static native int windowHeight();

  @JSBody(script = "return S4J.glfw.framebufferWidth();")
  private static native int framebufferWidth();

  @JSBody(script = "return S4J.glfw.framebufferHeight();")
  private static native int framebufferHeight();

  @JSBody(script = "return S4J.glfw.screenWidth();")
  private static native int screenWidth();

  @JSBody(script = "return S4J.glfw.screenHeight();")
  private static native int screenHeight();

  @JSBody(params = "on", script = "return S4J.glfw.fullScreen(on);")
  private static native boolean requestFullScreen(boolean on);

  @JSBody(params = {"w", "h", "argb", "hotX", "hotY"}, script = "return S4J.glfw.createCursor(w, h, argb, hotX, hotY);")
  private static native int createCursor(int w, int h, Int32Array argb, int hotX, int hotY);

  @JSBody(params = "id", script = "S4J.glfw.setCursor(id);")
  private static native void setCursor(int id);

  @JSBody(params = "id", script = "S4J.glfw.destroyCursor(id);")
  private static native void destroyCursor(int id);
}
