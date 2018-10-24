package eu.barkmin.processing.scratch;

import processing.core.PGraphics;

import java.util.ArrayList;

public class ScratchPen {

    private ScratchColor color = new ScratchColor();
    private float opacity = 255;
    private float size = 0;
    private ArrayList<Point> points = new ArrayList<>();
    private boolean down = false;

    public ScratchPen() {
    }

    /**
     * Copies a ScratchPen object.
     * @param p ScratchPen object to copy
     */
    public ScratchPen(ScratchPen p) {
        this.color = new ScratchColor(p.color);
        this.size = p.size;
        this.opacity = p.opacity;
        this.points = new ArrayList<>();
        this.down = p.down;
    }

    /**
     * Set color via hue value
     * @param h a hue value [0...255]
     */
    public void setColor(float h) {
        this.color.setHSB(h);
    }

    public void setColor(double h) {
        this.setColor((float) h);
    }

    /**
     * Set color via rgb values
     * @param r a red value [0...255]
     * @param g a green value [0...255]
     * @param b a blue value [0...255]
     */
    public void setColor(float r, float g, float b) {
        this.color.setRGB(r, g, b);
    }


    /**
     * Change color via a hue value, which is added to the current hue value
     * @param c a hue value [0...255]
     */
    public void changeColor(float c) {
        this.color.changeColor(c);
    }

    public void changeColor(double c) {
        this.changeColor((float) c);
    }

    /**
     * Set the size of the pen
     * @param size
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Returns the size of the pen
     * @return the size of the pen
     */
    public float getSize() {
        return this.size;
    }

    /**
     * Set the opacity
     * @param opacity
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * Set the position if the pen is down.
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setPosition(float x, float y) {
        if (this.down) {
            this.points.add(new Point(x, y, this.color, this.opacity, this.size));
        }
    }

    /**
     * Set the pen down.
     */
    public void down() {
        this.down = true;
    }

    /**
     * Move the pen up.
     */
    public void up() {
        this.down = false;
    }

    /**
     * Draw the line which the pen has drawn.
     */
    public void draw() {
        if (this.points.size() > 1) {
            PGraphics buffer = ScratchStage.getInstance().getPenBuffer();
            Point point = this.points.get(this.points.size() - 1);
            Point previousPoint = this.points.get(this.points.size() - 2);
            buffer.beginDraw();
            buffer.stroke(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
            buffer.strokeWeight(point.size);
            buffer.line(previousPoint.x, previousPoint.y, point.x, point.y);
            buffer.endDraw();
        }
    }
}

class Point {
    float x = 0;
    float y = 0;
    ScratchColor color;
    float opacity = 0;
    float size = 0;

    public Point(float x, float y, ScratchColor color, float opacity, float size) {
        this.x = x;
        this.y = y;
        this.color = new ScratchColor(color);
        this.opacity = opacity;
        this.size = size;
    }
}
