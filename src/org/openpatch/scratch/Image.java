package org.openpatch.scratch;

import processing.core.PImage;
import processing.core.PApplet;

import java.util.HashMap;

/**
 * The base class for representing scratch costumes and backdrops.
 */
public class Image implements Drawable {
    private String name;
    private PImage image;
    private final PImage originalImage;
    private Color tint = new Color();
    private float transparency = 255;

    private static final HashMap<String, PImage> originalImages = new HashMap<>();

    /**
     * Construct a ScratchImage object by a name and a path to an image.
     *
     * @param name      a name
     * @param imagePath a path to an image
     */
    public Image(String name, String imagePath) {
        this.name = name;
        this.originalImage = Image.loadImage(imagePath);
        this.image = this.originalImage;
    }

    public Image(String name, String spriteSheetPath, int x, int y, int width, int height) {
        this.name =name;
        this.originalImage = Image.loadImage(spriteSheetPath, x,y, width, height);
        this.image = this.originalImage;
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
    }

    private static PImage loadImage(String path) {
        PImage image = originalImages.get(path);
        if (image == null) {
            image = Stage.parent.loadImage(path);
            originalImages.put(path, image);
        }
        return image;
    }

    private static PImage loadImage(String path, int x, int y, int width, int height) {
        PImage image = originalImages.get(path);
        if (image == null) {
            image = Stage.parent.loadImage(path);
            originalImages.put(path, image);
        }
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
     * Returns the image
     *
     * @return the image
     */
    public PImage getImage() {
        return this.image;
    }

    /**
     * Sets the image from a given path
     *
     * @param imagePath the path to the image
     */
    public void setImage(String imagePath) {
        this.image = Stage.parent.loadImage(imagePath);
    }

    /**
     * Draw the scaled image at a given position.
     *
     * @param size a percentage value
     * @param degrees rotate
     * @param x    a x coordinate
     * @param y    a y coordinate
     */
    public void draw(float size, float degrees, float x, float y) {
        int newWidth = Math.round(this.originalImage.width * size / 100);
        int newHeight = Math.round(this.originalImage.height * size / 100);

        if (newWidth != this.image.width || newHeight != this.image.height) {
            this.image = this.originalImage.copy();
            this.image.resize(newWidth, newHeight);
        }

        PApplet parent = Stage.parent;
        parent.pushMatrix();
        parent.translate(x, y);
        parent.rotate(PApplet.radians(degrees));
        parent.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        parent.image(this.image, 0, 0);
        parent.noTint();
        if(Stage.getInstance().isDebug()) {
            parent.fill(Stage.DEBUG_COLOR[0], Stage.DEBUG_COLOR[1], Stage.DEBUG_COLOR[1]);
            parent.textAlign(parent.CENTER);
            parent.text("Rotation: " + degrees, 0, - newHeight/2.0f - 10);
            parent.text("(" + x + ", " + y + ")", 0, 0);
        }
        parent.popMatrix();

    }

    /**
     * Draw the image.
     */
    public void draw() {
        PApplet parent = Stage.parent;
        parent.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        parent.image(this.image, 0, 0);
        parent.noTint();
    }

    /**
     * Draw the image as a background. The image is automatically scaled to fit the window size.
     */
    public void drawAsBackground() {
        PApplet parent = Stage.parent;

        int newWidth = parent.width;
        int newHeight = parent.height;
        if (newWidth != this.image.width || newHeight != this.image.height) {
            this.image = this.originalImage.copy();
            this.image.resize(newWidth, newHeight);
        }
        parent.tint(this.tint.getRed(), this.tint.getGreen(), this.tint.getBlue(), this.transparency);
        parent.image(this.image, newWidth / 2, newHeight / 2);
        parent.noTint();
    }
}
