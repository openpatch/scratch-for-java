package org.openpatch.scratch;

import processing.core.*;

class Applet extends PApplet {

  private boolean isRunning = false;
  private final int INITIAL_HEIGHT;
  private final int INITIAL_WIDTH;
  private String renderer = Renderer.JAVA;

  public Applet() {
    this(480, 360);
  }

  public Applet(int width, int height) {
    super();
    this.INITIAL_HEIGHT = height;
    this.INITIAL_WIDTH = width;
  }

  public Applet(int width, int height, String renderer) {
    this(width, height);
    this.renderer = renderer;
  }

  /**
   *
   * Use this method to call size().
   *
   * @see PApplet#fullScreen()
   * @see PApplet#setup()
   * @see PApplet#size(int,int)
   * @see PApplet#smooth()
   */
  public void settings() {
    this.size(this.INITIAL_WIDTH, this.INITIAL_HEIGHT, this.renderer);
  }

  /**
   * Pauses the sketch.
   */
  public void pauseSketch() {
    this.noLoop();
  }

  /**
   * Resumes the sketch.
   */
  public void resumeSketch() {
    this.loop();
  }

  /**
   * Forces a redraw, even if the sketch is paused.
   */
  public void redrawSketch() {
    this.redraw();
  }

  public void runSketch() {
    if (!isRunning) {
      super.runSketch();
      isRunning = true;
    }
  }

  public void setup() {
    this.surface.setTitle("Scratch for Java");
  }

  public void draw() {}
}
