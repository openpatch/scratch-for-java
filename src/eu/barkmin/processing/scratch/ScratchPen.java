package eu.barkmin.processing.scratch;

import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Iterator;

public class ScratchPen {

    private ScratchColor color = new ScratchColor(120);
    private float opacity = 255;
    private float size = 1;
    private Stack<ArrayList<Point>> pointsBuffer = new Stack<>();
    private boolean down = false;

    public ScratchPen() {
    }

    /**
     * Copies a ScratchPen object.
     * 
     * @param p ScratchPen object to copy
     */
    public ScratchPen(ScratchPen p) {
        this.color = new ScratchColor(p.color);
        this.size = p.size;
        this.opacity = p.opacity;
        this.pointsBuffer = new Stack<>();
        this.pointsBuffer.add(new ArrayList<>());
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
     * Set the opacity
     * 
     * @param opacity opacity of the pen
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
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
                    .add(new Point(x, y, this.color, this.opacity, this.size));
        }
    }

    /**
     * Set the pen down.
     */
    public void down() {
        if (!this.down) {
            this.pointsBuffer.add(new ArrayList<>());
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
        ScratchStage.getInstance().eraseAll();
    }

    /**
     * Draw the line which the pen has drawn.
     */
    public void draw() {
        PGraphics buffer = ScratchStage.getInstance().getPenBuffer();
        int pointsBufferSize = this.pointsBuffer.size();
        if (pointsBufferSize <= 0)
            return;

        Iterator<ArrayList<Point>> pointsBufferIter = this.pointsBuffer.iterator();

        buffer.beginDraw();

        while (pointsBufferIter.hasNext()) {
            ArrayList<Point> points = pointsBufferIter.next();
            int pointsSize = points.size();

            if (pointsSize > 1) {
                Point point = points.get(pointsSize - 1);
                Point previousPoint = points.get(pointsSize - 2);
                buffer.stroke(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
                buffer.strokeWeight(point.size);
                buffer.line(previousPoint.x, previousPoint.y, point.x, point.y);
            } else if (pointsSize == 1) {
                Point point = points.get(pointsSize - 1);
                buffer.stroke(point.color.getRed(), point.color.getGreen(), point.color.getBlue(), point.opacity);
                buffer.strokeWeight(point.size);
                buffer.point(point.x, point.y);
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
    ScratchColor color;
    float opacity;
    float size;

    Point(float x, float y, ScratchColor color, float opacity, float size) {
        this.x = x;
        this.y = y;
        this.color = new ScratchColor(color);
        this.opacity = opacity;
        this.size = size;
    }
}
