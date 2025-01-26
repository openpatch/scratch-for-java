package org.openpatch.scratch.internal;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.color.Color;
import org.openpatch.scratch.extensions.shader.Shader;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/** The base class for representing scratch costumes and backdrops. */
public class Image {

  String name;
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
  }

  /**
   * Construct a ScratchImage object by a name and a path to a sprite sheet.
   *
   * @param name a name
   * @param spriteSheetPath a path to a sprite sheet
   * @param x the x coordinate of the tile
   * @param y the y coordinate of the tile
   * @param width the width of the tile
   * @param height the height of the tile
   */
  public Image(String name, String spriteSheetPath, int x, int y, int width, int height) {
    this.name = name;
    this.originalImage = Image.loadImage(spriteSheetPath, x, y, width, height);
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
    this.originalImage = i.originalImage;
    this.tint = new Color(i.tint);
    this.transparency = i.transparency;
    this.width = i.width;
    this.height = i.height;
  }

  /**
   * Loads an image from a given path and returns it.
   *
   * @param path the path to the image
   * @return the image
   */
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

  /**
   * Loads an image from a given path and returns a tile of the image.
   *
   * @param path the path to the image
   * @param x the x coordinate of the tile
   * @param y the y coordinate of the tile
   * @param width the width of the tile
   * @param height the height of the tile
   * @return a tile of the image
   */
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

  /**
   * Returns the width
   *
   * @return the width
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * Returns the height
   *
   * @return the height
   */
  public int getHeight() {
    return this.height;
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
   * @return the tint
   */
  public Color getTint() {
    return this.tint;
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
   * Sets the size of the image to the specified percentage of the original size.
   *
   * @param percentage The desired size as a percentage of the original size
   */
  public void setSize(double percentage) {
    var width = (int) Math.round(this.originalImage.width * percentage / 100);
    var height = (int) Math.round(this.originalImage.height * percentage / 100);
    this.setSize(width, height);
  }

  /**
   * Sets the size of the image to the specified width and height. If a resized version with the
   * requested dimensions already exists in cache, it will use that version. Otherwise, it creates a
   * new resized copy from the original image and caches it for future use.
   *
   * @param width The desired width of the image in pixels
   * @param height The desired height of the image in pixels
   */
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Draw the scaled image at a given position.
   *
   * @param buffer a buffer
   * @param size a percentage value
   * @param degrees direction
   * @param x a x coordinate
   * @param y a y coordinate
   * @param style a rotation style
   * @param shader a shader
   */
  public void draw(
      PGraphics buffer,
      double size,
      double degrees,
      double x,
      double y,
      RotationStyle style,
      Shader shader) {
    buffer.push();
    buffer.translate((float) x, (float) -y);
    degrees -= 90;
    switch (style) {
      case DONT:
        break;
      case ALL_AROUND:
        buffer.rotate(PApplet.radians((float) degrees));
        break;
      case LEFT_RIGHT:
        if (degrees > -90 && degrees < 90) {
          buffer.scale(1, 1);
        } else {
          buffer.scale(-1, 1);
        }
        break;
    }
    if (shader != null) {
      var pshader = shader.getPShader();
      buffer.shader(pshader);
    }

    buffer.translate(-this.width / 2.0f, -this.height / 2.0f);
    buffer.tint(
        (float) this.tint.getRed(),
        (float) this.tint.getGreen(),
        (float) this.tint.getBlue(),
        (float) this.transparency);
    buffer.noStroke();
    buffer.textureMode(PConstants.NORMAL);
    buffer.beginShape();
    buffer.texture(this.originalImage);
    buffer.vertex(0, 0, 0, 0);
    buffer.vertex(this.width, 0, 1, 0);
    buffer.vertex(this.width, this.height, 1, 1);
    buffer.vertex(0, this.height, 0, 1);
    buffer.endShape();
    buffer.noTint();
    buffer.resetShader();
    buffer.pop();
  }

  /**
   * Draw the scaled image at a given position.
   *
   * @param size a percentage value
   * @param degrees direction
   * @param x a x coordinate
   * @param y a y coordinate
   * @param style a rotation style
   */
  public void drawDebug(
      PGraphics buffer, double size, double degrees, double x, double y, RotationStyle style) {
    buffer.push();
    buffer.translate((float) x, (float) -y);
    buffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
    buffer.textAlign(PConstants.CENTER);
    buffer.text("Direction: " + Math.round((degrees) * 100) / 100.0, 0, -this.height / 2.0f - 10);
    buffer.text("(" + Math.round(x * 100) / 100.0 + ", " + Math.round(y * 100) / 100.0 + ")", 0, 0);
    buffer.pop();
  }

  /**
   * Draw the scaled image at a given position.
   *
   * @param buffer a buffer
   * @param size a percentage value
   * @param degrees direction
   * @param x a x coordinate
   * @param y a y coordinate
   * @param shader a shader
   */
  public void draw(PGraphics buffer, float size, float degrees, float x, float y, Shader shader) {
    this.draw(buffer, size, degrees, x, y, RotationStyle.ALL_AROUND, shader);
  }

  /**
   * Draw the image as a background. The image is automatically scaled to fit the window size.
   *
   * @param buffer a buffer
   */
  public void drawAsBackground(PGraphics buffer) {
    buffer.tint(
        (float) this.tint.getRed(),
        (float) this.tint.getGreen(),
        (float) this.tint.getBlue(),
        (float) this.transparency);
    buffer.noStroke();
    buffer.textureMode(PConstants.NORMAL);
    buffer.beginShape();
    buffer.texture(this.originalImage);
    buffer.vertex(0, 0, 0, 0);
    buffer.vertex(buffer.width, 0, 1, 0);
    buffer.vertex(buffer.width, buffer.height, 1, 1);
    buffer.vertex(0, buffer.height, 0, 1);
    buffer.endShape();
    buffer.noTint();
  }
}
