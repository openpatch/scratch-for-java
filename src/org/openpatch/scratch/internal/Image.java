package org.openpatch.scratch.internal;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Window;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/** The base class for representing scratch costumes and backdrops. */
public class Image {

  String name;
  PImage image;
  AbstractMap<Float, PImage> imageResized = new ConcurrentHashMap<>();
  final PImage originalImage;
  Color tint = new Color();
  double transparency = 255;

  private int width = 0;
  private int height = 0;

  private static final AbstractMap<String, PImage> originalImages = new ConcurrentHashMap<>();
  private static final AbstractMap<String, PImage> originalImageTiles = new ConcurrentHashMap<>();

  /**
   * Construct a ScratchImage object by a name and a path to an image.
   *
   * @param name a a name
   * @param imagePath a path to an image
   */
  public Image(String name, String imagePath) {
    this.name = name;
    this.originalImage = Image.loadImage(imagePath);
    this.width = this.originalImage.width;
    this.height = this.originalImage.height;
    this.image = this.originalImage;
  }

  public Image(String name, String spriteSheetPath, int x, int y, int width, int height) {
    this.name = name;
    this.originalImage = Image.loadImage(spriteSheetPath, x, y, width, height);
    this.image = this.originalImage;
    this.width = this.originalImage.width;
    this.height = this.originalImage.height;
  }

  /**
   * Copies a ScratchImage object
   *
   * @param i the ScratchImage object to copy
   */
  public Image(Image i) {
    this.name = i.name;
    this.image = i.image;
    this.originalImage = i.originalImage;
    this.tint = new Color(i.tint);
    this.transparency = i.transparency;
    this.width = i.width;
    this.height = i.height;
  }

  public static PImage loadImage(String path) {
    PImage image = originalImages.get(path);
    if (image == null) {
      // add support for ~
      path = path.replaceFirst("^~", System.getProperty("user.home"));
      image = Applet.getInstance().loadImage(path);
      originalImages.put(path, image);
    }
    return image;
  }

  public static PImage loadImage(String path, int x, int y, int width, int height) {
    String key = path + "x" + x + "y" + y + "w" + width + "h" + height;
    PImage image = originalImageTiles.get(key);
    if (image == null) {
      image = loadImage(path);
      image = image.get(x, y, width, height);
      originalImageTiles.put(key, image);
    }
    return image;
  }

  /**
   * Returns the name
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name
   *
   * @param name unique name
   */
  public void setName(String name) {
    this.name = name;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public int getPixel(int x, int y) {
    return this.image.get(x, y);
  }

  /**
   * Sets the tint with rgb
   *
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setTint(double r, double g, double b) {
    this.tint.setRGB(r, g, b);
  }

  /**
   * Sets the tint with hue
   *
   * @param h a hue value [0...255]
   */
  public void setTint(double h) {
    this.tint.setHSB(h);
  }

  /**
   * Changes the tint by adding a hue value to the current hue value.
   *
   * @param h a hue value [0...255]
   */
  public void changeTint(double h) {
    this.tint.changeColor(h);
  }

  /**
   * Returns the tint
   *
   * @return a hue value [0...255]
   */
  public double getTint() {
    return this.tint.getHSB();
  }

  /**
   * Sets the transparency. 0 equals fully transparent.
   *
   * @param transparency [0...255]
   */
  public void setTransparency(double transparency) {
    this.transparency = transparency;
  }

  /**
   * Changes the transparency by adding a step value to the current transparency.
   *
   * @param step a step value
   */
  public void changeTransparency(double step) {
    this.setTransparency((this.transparency + step) % 255);
  }

  /**
   * Returns the transparency
   *
   * @return the transparency [0...255]
   */
  public double getTransparency() {
    return this.transparency;
  }

  /**
   * Sets the image from a given path
   *
   * @param imagePath the path to the image
   */
  public void setImage(String imagePath) {
    this.image = Applet.getInstance().loadImage(imagePath);
  }

  public void setSize(double percentage) {
    this.width = (int) Math.round(this.originalImage.width * percentage / 100);
    this.height = (int) Math.round(this.originalImage.height * percentage / 100);

    var imageResized = this.imageResized.get(percentage);
    if (imageResized != null) {
      this.image = imageResized;
    } else {
      imageResized = this.originalImage.copy();
      imageResized.resize(this.width, this.height);
      this.image = imageResized;
    }
  }

  public void setSize(int width, int height) {
    var imageResized = this.originalImage.copy();
    imageResized.resize(width, height);
    this.image = imageResized;
  }

  /**
   * Draw the scaled image at a given position.
   *
   * @param size a percentage value
   * @param degrees direction
   * @param x a x coordinate
   * @param y a y coordinate
   */
  public void draw(double size, double degrees, double x, double y, RotationStyle style) {
    Applet applet = Applet.getInstance();
    PGraphics g = applet.getGraphics();
    g.push();
    g.imageMode(PConstants.CENTER);
    g.translate(
        (float) x + Window.getInstance().getWidth() / 2,
        (float) -y + Window.getInstance().getHeight() / 2);
    degrees -= 90;
    switch (style) {
      case DONT:
        break;
      case ALL_AROUND:
        g.rotate(PApplet.radians((float) degrees));
        break;
      case LEFT_RIGHT:
        if (degrees > -90 && degrees < 90) {
          g.scale(1, 1);
        } else {
          g.scale(-1, 1);
        }
        break;
    }
    g.tint(
        (float) this.tint.getRed(),
        (float) this.tint.getGreen(),
        (float) this.tint.getBlue(),
        (float) this.transparency);
    g.image(this.image, 0, 0);
    g.noTint();
    g.pop();
  }

  public void drawDebug(
      PGraphics buffer, double size, double degrees, double x, double y, RotationStyle style) {
    buffer.push();
    buffer.translate(
        (float) x + Window.getInstance().getWidth() / 2,
        (float) -y + Window.getInstance().getHeight() / 2);
    buffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
    buffer.textAlign(PConstants.CENTER);
    buffer.text("Direction: " + (degrees + 90), 0, -this.height / 2.0f - 10);
    buffer.text("(" + x + ", " + y + ")", 0, 0);
    buffer.pop();
  }

  public void draw(float size, float degrees, float x, float y) {
    this.draw(size, degrees, x, y, RotationStyle.ALL_AROUND);
  }

  /** Draw the image. */
  public void draw() {
    PApplet parent = Applet.getInstance();
    parent.tint(
        (float) this.tint.getRed(),
        (float) this.tint.getGreen(),
        (float) this.tint.getBlue(),
        (float) this.transparency);
    parent.image(this.image, 0, 0);
    parent.noTint();
  }

  /** Draw the image as a background. The image is automatically scaled to fit the window size. */
  public void drawAsBackground() {
    PApplet parent = Applet.getInstance();
    parent.tint(
        (float) this.tint.getRed(),
        (float) this.tint.getGreen(),
        (float) this.tint.getBlue(),
        (float) this.transparency);
    parent.image(this.image, parent.width / 2, parent.height / 2);
    parent.noTint();
  }
}
