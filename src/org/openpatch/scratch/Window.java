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

  /**
   * Gets the seconds passed since the last frame.
   * 
   * @return seconds since last frame
   */
  public float getDeltaTime() {
    return Applet.getInstance().getDeltaTime();
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

  public void setDefaultFont(String path) {
    Font.defaultFontPath = path;
  }

  public void setStage(Stage stage) {
    Applet.getInstance().setStage(stage);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name  Name of the stage
   * @param stage A stage object
   */
  public void addStage(String name, Stage stage) {
    Applet.getInstance().addStage(name, stage);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  public void switchStage(String name) {
    Applet.getInstance().switchStage(name);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  public void removeStage(String name) {
    Applet.getInstance().removeStage(name);
  }

  public Stage getStage() {
    return Applet.getInstance().getStage();
  }

  public void exit() {
    this.whenExits();
    Applet.getInstance().exit();
  }

  public void whenExits() {

  }
}
