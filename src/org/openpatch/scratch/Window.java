package org.openpatch.scratch;

import org.openpatch.scratch.internal.*;

public class Window {

  public static final int[] DEBUG_COLOR = {255, 0, 0};
  private static Window instance;

  public Window() {
    this(480, 360);
  }

  public Window(final String assets) {
    this(480, 360, assets);
  }

  public Window(final int width, final int height) {
    this(width, height, null);
  }

  public Window(final int width, final int height, final String assets) {
    super();
    if (Window.instance != null) {
      throw new Error("You can only have one Window.");
    }

    Window.instance = this;
    new Applet(width, height, assets);
  }

  public static Window getInstance() {
    return instance;
  }

  public boolean isDebug() {
    return Applet.getInstance().isDebug();
  }

  public void setDebug(final boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  public int getWidth() {
    return Applet.getInstance().getWidth();
  }

  public int getHeight() {
    return Applet.getInstance().getHeight();
  }

  public void addStage(final String name, final Stage stage) {
    Applet.getInstance().addStage(name, stage);
  }

  public Stage getStage(final String name) {
    return Applet.getInstance().getStage(name);
  }

  public void removeStage(final String name) {
    Applet.getInstance().removeStage(name);
  }

  public void switchStage(final String name) {
    Applet.getInstance().switchStage(name);
  }

  public void exit() {
    Applet.getInstance().exit();
  }
}
