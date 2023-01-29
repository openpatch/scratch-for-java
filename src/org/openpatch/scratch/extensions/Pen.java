package org.openpatch.scratch.extensions;

import org.openpatch.scratch.Color;
import org.openpatch.scratch.Drawable;
import org.openpatch.scratch.Stage;
import processing.core.PGraphics;

import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;

public class Pen implements Drawable {

    private Color color = new Color(0,0,0);
    private float transparency = 255;
    private float size = 1;
    private Stack<CopyOnWriteArrayList<Point>> pointsBuffer = new Stack<>();
    private boolean down = false;

    public Pen() {
    }

    /**
     * Copies a Pen object.
     * 
     * @param p Pen object to copy
     */
    public Pen(Pen p) {
        this.color = new Color(p.color);
        this.size = p.size;
        this.transparency = p.transparency;
        this.pointsBuffer = new Stack<>();
        this.pointsBuffer.add(new CopyOnWriteArrayList<>());
        this.down = p.down;
    }

    public float getColor() {
        return this.color.getHSB();
    }

    /**
     * Set color via hue value
     * 
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
     * 
     * @param r a red value [0...255]
     * @param g a green value [0...255]
     * @param b a blue value [0...255]
     */
    public void setColor(float r, float g, float b) {
        this.color.setRGB(r, g, b);
    }

    /**
     * Change color via a hue value, which is added to the current hue value
     * 
     * @param c a hue value [0...255]
     */
    public void changeColor(float c) {
        this.color.changeColor(c);
    }

    public void changeColor(double c) {
        this.color.changeColor((float) c);
    }

    /**
     * Set the size of the pen
     * 
     * @param size size of the pen
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Returns the size of the pen
     * 
     * @return the size of the pen
     */
    public float getSize() {
        return this.size;
    }

    /**
     * Changes the size of the pen
     */
    public void changeSize(float size) {
        this.size += size;
    }

    /**
     * Set the transparency
     * 
     * @param transparency transparency of the pen
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Set the position if the pen is down.
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setPosition(float x, float y) {
        if (this.down) {
            this.pointsBuffer.get(this.pointsBuffer.size() - 1)
                    .add(new Point(x, y, this.color, this.transparency, this.size));
        }
    }

    /**
     * Set the pen down.
     */
    public void down() {
        if (!this.down) {
            this.pointsBuffer.add(new CopyOnWriteArrayList<>());
        }
        this.down = true;
    }

    /**
     * Move the pen up.
     */
    public void up() {
        this.down = false;
    }

    public void eraseAll() {
        Stage.getInstance().eraseAll();
    }

    /**
     * Draw the line which the pen has drawn.
     */
    public void draw() {
        PGraphics buffer = Stage.getInstance().getPenBuffer();
        int pointsBufferSize = this.pointsBuffer.size();
        if (pointsBufferSize <= 0)
            return;

        Iterator<CopyOnWriteArrayList<Point>> pointsBufferIter = this.pointsBuffer.iterator();

        buffer.beginDraw();

        while (pointsBufferIter.hasNext()) {
            CopyOnWriteArrayList<Point> points = pointsBufferIter.next();
            Iterator<Point> pointsIter = points.iterator();

            Point previousPoint = null;
            int pointsSize = points.size();
            while (pointsIter.hasNext()) {
                Point point = pointsIter.next();
                if (pointsSize > 1 && previousPoint != null) {
                    buffer.stroke(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
                    buffer.strokeWeight(point.size);
                    buffer.line(previousPoint.x, previousPoint.y, point.x, point.y);
                } else if (pointsSize == 1) {
                    buffer.stroke(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
                    buffer.fill(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
                    buffer.strokeWeight(point.size);
                    buffer.circle(point.x, point.y, point.size);
                }
                previousPoint = point;
            }
            if (!this.down || pointsBufferSize > 1) {
                pointsBufferIter.remove();
            }
        }
        buffer.endDraw();
    }
}

class Point {
    float x;
    float y;
    Color color;
    float opacity;
    float size;

    Point(float x, float y, Color color, float opacity, float size) {
        this.x = x;
        this.y = y;
        this.color = new Color(color);
        this.opacity = opacity;
        this.size = size;
    }
}
