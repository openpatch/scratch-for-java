package org.openpatch.scratch.extensions.hitbox;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.internal.Applet;
import processing.core.PConstants;

public class Hitbox {
  private final Polygon originalPolygon;
  private Polygon polygon;

  public Hitbox(Polygon polygon) {
    this.originalPolygon = polygon;
    this.polygon = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
  }

  public Hitbox(int[] xPoints, int[] yPoints) {
    this.originalPolygon = new Polygon(xPoints, yPoints, xPoints.length);
    this.polygon = new Polygon(xPoints, yPoints, xPoints.length);
  }

  public Polygon getPolygon() {
    return polygon;
  }

  public void translateAndRotateAndResize(
      float degrees, float originX, float originY, float translateX, float translateY, float size) {
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
      float[] rotatedXY = Stage.rotateXY(xPoint, yPoint, originX, originY, degrees);
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
    Applet applet = Applet.getInstance();
    applet.stroke(r, g, b);
    applet.strokeWeight(2);
    applet.noFill();
    applet.beginShape();
    for (int i = 0; i < xPoints.length; i++) {
      applet.vertex(xPoints[i], yPoints[i]);
    }
    applet.endShape(PConstants.CLOSE);
  }

  public void draw() {
    draw(polygon, Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
  }

  public boolean contains(float x, float y) {
    return polygon.contains(new Point(Math.round(x), Math.round(y)));
  }

  public boolean intersects(Hitbox hitbox) {
    if (this.getPolygon().getBounds().intersects(hitbox.getPolygon().getBounds())) {

      Area a = new Area(this.getPolygon());
      Area b = new Area(hitbox.getPolygon());

      a.intersect(b);

      return !a.isEmpty();
    }
    return false;
  }
}
