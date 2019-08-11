package eu.barkmin.processing.scratch;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

public class ScratchHitbox {
    private Polygon originalPolygon;
    private Polygon polygon;

    public ScratchHitbox(int[] xPoints, int[] yPoints) {
        this.originalPolygon = new Polygon(xPoints, yPoints, xPoints.length);
        this.polygon = new Polygon(xPoints, yPoints, xPoints.length);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void translateAndRotateAndResize(float degrees, float originX, float originY, float translateX, float translateY, float size) {
        Polygon polygon;
        polygon = this.scale(size, this.originalPolygon);
        polygon = this.translate(translateX, translateY, polygon);
        polygon = this.rotate(degrees, originX, originY, polygon);
        this.polygon = polygon;
    }

    /**
     * @param size Percentage in 100
     * @return the scaled polygon
     */
    private Polygon scale(float size, Polygon polygon) {
        int[] xPoints = new int[polygon.xpoints.length];
        int[] yPoints = new int[polygon.ypoints.length];

        for (int i = 0; i < polygon.xpoints.length; i++) {
            int xPoint = polygon.xpoints[i];
            int yPoint = polygon.ypoints[i];

            int scaledX = Math.round(xPoint);
            int scaledY = Math.round(yPoint);

            xPoints[i] = Math.round(scaledX * size / 100.0f);
            yPoints[i] = Math.round(scaledY * size / 100.0f);
        }
        return new Polygon(xPoints, yPoints, xPoints.length);
    }

    private Polygon rotate(float degrees, float originX, float originY, Polygon polygon) {
        int[] xPoints = new int[polygon.xpoints.length];
        int[] yPoints = new int[polygon.ypoints.length];

        for (int i = 0; i < polygon.xpoints.length; i++) {
            int xPoint = polygon.xpoints[i];
            int yPoint = polygon.ypoints[i];
            float[] rotatedXY = ScratchStage.rotateXY(xPoint, yPoint, originX, originY, degrees);
            xPoints[i] = Math.round(rotatedXY[0]);
            yPoints[i] = Math.round(rotatedXY[1]);
        }

        return new Polygon(xPoints, yPoints, xPoints.length);
    }

    private Polygon translate(float x, float y, Polygon polygon) {
        int[] xPoints = new int[polygon.xpoints.length];
        int[] yPoints = new int[polygon.ypoints.length];

        for (int i = 0; i < polygon.xpoints.length; i++) {
            int xPoint = polygon.xpoints[i];
            int yPoint = polygon.ypoints[i];

            int translatedX = Math.round(xPoint + x);
            int translatedY = Math.round(yPoint + y);

            xPoints[i] = translatedX;
            yPoints[i] = translatedY;
        }
        return new Polygon(xPoints, yPoints, xPoints.length);
    }

    private void draw(Polygon polygon, float r, float g, float b) {
        int[] xPoints = polygon.xpoints;
        int[] yPoints = polygon.ypoints;
        ScratchStage.parent.stroke(r, g, b);
        ScratchStage.parent.strokeWeight(2);
        ScratchStage.parent.noFill();
        ScratchStage.parent.beginShape();
        for (int i = 0; i < xPoints.length; i++) {
            ScratchStage.parent.vertex(xPoints[i], yPoints[i]);
        }
        ScratchStage.parent.endShape(ScratchStage.parent.CLOSE);
    }

    public void draw() {
        draw(polygon, ScratchStage.DEBUG_COLOR[0], ScratchStage.DEBUG_COLOR[1], ScratchStage.DEBUG_COLOR[2]);
    }

    public boolean contains(float x, float y) {
        return polygon.contains(new Point(Math.round(x), Math.round(y)));
    }

    public boolean intersects(ScratchHitbox hitbox) {
        Area a = new Area(this.getPolygon());
        Area b = new Area(hitbox.getPolygon());

        a.intersect(b);

        return !a.isEmpty();
    }
}
