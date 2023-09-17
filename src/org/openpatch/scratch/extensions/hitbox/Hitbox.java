package org.openpatch.scratch.extensions.hitbox;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.math.Utils;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Hitbox {
  private final Shape originalShape;
  private Shape shape;

  public Hitbox(Shape shape) {
    this.originalShape = shape;
    this.shape = shape;
  }

  public Hitbox(int[] xPoints, int[] yPoints) {
    this.originalShape = new Polygon(xPoints, yPoints, xPoints.length);
    this.shape = new Polygon(xPoints, yPoints, xPoints.length);
  }

  public Shape getShape() {
    return this.shape;
  }

  public void translateAndRotateAndResize(
      float degrees, float originX, float originY, float translateX, float translateY, float size) {

    AffineTransform tx = new AffineTransform();
    tx.scale(size / 100.0, size / 100.0);
    this.shape = tx.createTransformedShape(this.originalShape);

    tx = new AffineTransform();
    tx.translate(translateX, translateY);
    this.shape = tx.createTransformedShape(this.shape);

    tx = new AffineTransform();
    tx.rotate(Utils.degreesToRadians(degrees), originX, originY);
    this.shape = tx.createTransformedShape(this.shape);
  }

  private void drawDebug(PGraphics buffer, float r, float g, float b) {
    buffer.push();
    buffer.stroke(r, g, b);
    buffer.strokeWeight(2);
    buffer.noFill();
    buffer.translate(buffer.width / 2, buffer.height / 2);
    drawShape(buffer);
    buffer.pop();
  }

  public void drawDebug(PGraphics buffer) {
    this.drawDebug(buffer, Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
  }

  public boolean contains(float x, float y) {
    return this.shape.contains(new Point(Math.round(x), Math.round(y)));
  }

  public boolean intersects(Hitbox hitbox) {
    if (this.getShape().getBounds().intersects(hitbox.getShape().getBounds())) {

      Area a = new Area(this.getShape());
      Area b = new Area(hitbox.getShape());

      a.intersect(b);

      return !a.isEmpty();
    }
    return false;
  }

  public void drawShape(PGraphics buffer) {
    if (shape == null) {
      return;
    }
    PathIterator path = this.shape.getPathIterator(null, 1);
    final float coord[] = new float[6];
    boolean began = false;
    while (!path.isDone()) {
      final int code = path.currentSegment(coord);
      if (code == PathIterator.SEG_MOVETO) {
        buffer.beginShape();
        began = true;
        buffer.vertex(coord[0], coord[1]);
      } else if (code == PathIterator.SEG_LINETO) {
        buffer.vertex(coord[0], coord[1]);
      } else if (code == PathIterator.SEG_CLOSE) {
        buffer.endShape(PConstants.CLOSE);
        began = false;
      } else if (code == PathIterator.SEG_CUBICTO) {
        buffer.vertex(coord[0], coord[1]);
        buffer.bezierVertex(coord[0], coord[1], coord[2], coord[3], coord[4], coord[5]);
      } else if (code == PathIterator.SEG_QUADTO) {
        buffer.quadraticVertex(coord[0], coord[1], coord[2], coord[3]);
      }
      path.next();
    }
    if (began) {
      buffer.endShape();
    }
  }
}
