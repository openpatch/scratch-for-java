package org.openpatch.scratch;

import org.openpatch.scratch.internal.*;

public class Window {

  public static final int[] DEBUG_COLOR = { 255, 0, 0 };
  private static Window instance;

  public Window() {
    this(480, 360);
  }

  public Window(String assets) {
    this(480, 360, assets);
  }

  public Window(int width, final int height) {
    this(width, height, null);
  }

  public Window(int width, final int height, final String assets) {
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

  public void setDebug(boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  public int getWidth() {
    return Applet.getInstance().getWidth();
  }

  public int getHeight() {
    return Applet.getInstance().getHeight();
  }

  public void addStage(String name, final Stage stage) {
    Applet.getInstance().addStage(name, stage);
  }

  public Stage getStage(String name) {
    return Applet.getInstance().getStage(name);
  }

  public void removeStage(String name) {
    Applet.getInstance().removeStage(name);
  }

  public void switchStage(String name) {
    Applet.getInstance().switchStage(name);
  }

  public void exit() {
    Applet.getInstance().exit();
  }
}
