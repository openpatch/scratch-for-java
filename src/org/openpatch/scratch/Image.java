package org.openpatch.scratch;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The base class for representing scratch costumes and backdrops.
 */
public class Image implements Drawable {
    private String name;
    private PImage image;
    private final PImage originalImage;
    private Color tint = new Color();
    private float transparency = 255;

    private int width = 0;
    private int height = 0;

    private static final ConcurrentHashMap<String, PImage> originalImages = new ConcurrentHashMap<>();

    /**
     * Construct a ScratchImage object by a name and a path to an image.
     *
     * @param name      a a name
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

    public void addedToStage(Stage stage) {

    }

    public void removedFromStage(Stage stage) {
    }

    protected static PImage loadImage(String path) {
        PImage image = originalImages.get(path);
        if (image == null) {
            image = Applet.getInstance().loadImage(path);
            originalImages.put(path, image);
        } 
        return image;
    }

    protected static PImage loadImage(String path, int x, int y, int width, int height) {
        var image = Image.loadImage(path);
        return image.get(x, y, width, height);
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
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y) {
        return image.get(x, y);
    }

    /**
     * Sets the tint with rgb
     *
     * @param r a red value [0...255]
     * @param g a green value [0...255]
     * @param b a blue value [0...255]
     */
    public void setTint(float r, float g, float b) {
        this.tint.setRGB(r, g, b);
    }

    /**
     * Sets the tint with hue
     *
     * @param h a hue value [0...255]
     */
    public void setTint(float h) {
        this.tint.setHSB(h);
    }

    /**
     * Changes the tint by adding a hue value to the current hue value.
     *
     * @param h a hue value [0...255]
     */
    public void changeTint(float h) {
        this.tint.changeColor(h);
    }

    /**
     * Returns the tint
     *
     * @return a hue value [0...255]
     */
    public float getTint() {
        return this.tint.getHSB();
    }

    /**
     * Sets the transparency. 0 equals fully transparent.
     *
     * @param transparency [0...255]
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Changes the transparency by adding a step value to the current transparency.
     *
     * @param step a step value
     */
    public void changeTransparency(float step) {
        this.setTransparency((this.transparency + step) % 255);
    }

    /**
     * Returns the transparency
     *
     * @return the transparency [0...255]
     */
    public float getTransparency() {
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

    public void setSize(float percentage) {
        this.width = Math.round(this.originalImage.width * percentage / 100);
        this.height = Math.round(this.originalImage.height * percentage / 100);
        this.image = this.originalImage.copy();
        this.image.resize(width, height);
    }

    /**
     * Draw the scaled image at a given position.
     *
     * @param size    a percentage value
     * @param degrees direction
     * @param x       a x coordinate
     * @param y       a y coordinate
     */
    public void draw(float size, float degrees, float x, float y, RotationStyle style) {
        Applet applet = Applet.getInstance();
        applet.pushMatrix();
        applet.translate(x, y);
        switch (style) {
            case DONT:
                break;
            case ALL_AROUND:
                applet.rotate(PApplet.radians(degrees));
                break;
            case LEFT_RIGHT:
                if (degrees > -90 && degrees < 90) {
                    applet.scale(1, 1);
                } else {
                    applet.scale(-1, 1);
                }
                break;
        }
        applet.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        applet.image(this.image, 0, 0);
        applet.noTint();
        if (applet.isDebug()) {
            applet.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[1]);
            applet.textAlign(PConstants.CENTER);
            applet.text("Direction: " + degrees, 0, -height / 2.0f - 10);
            applet.text("(" + x + ", " + y + ")", 0, 0);
        }
        applet.popMatrix();

    }

    public void draw(float size, float degrees, float x, float y) {
        draw(size, degrees, x, y, RotationStyle.ALL_AROUND);
    }

    /**
     * Draw the image.
     */
    public void draw() {
        PApplet parent = Applet.getInstance();
        parent.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        parent.image(this.image, 0, 0);
        parent.noTint();
    }

    /**
     * Draw the image as a background. The image is automatically scaled to fit the
     * window size.
     */
    public void drawAsBackground() {
        PApplet parent = Applet.getInstance();
        parent.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        parent.image(this.image, parent.width / 2, parent.height / 2);
        parent.noTint();
    }
}
