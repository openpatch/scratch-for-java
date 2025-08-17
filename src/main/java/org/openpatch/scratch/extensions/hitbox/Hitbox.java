package org.openpatch.scratch.extensions.hitbox;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.shape.Shape;
import org.openpatch.scratch.extensions.shape.Bounds;
import org.openpatch.scratch.extensions.shape.Polygon;

import processing.core.PGraphics;

/**
 * Represents a hitbox with a shape that can be transformed by scaling,
 * translating, and rotating
 * it.
 */
public class Hitbox {
  private final Shape originalShape;
  private Shape shape;

  /**
   * Constructs a hitbox with the specified shape.
   *
   * @param shape The shape of the hitbox.
   */
  public Hitbox(Shape shape) {
    this.originalShape = shape;
    this.shape = shape;
  }

  /**
   * Constructs a hitbox with the specified x and y points.
   *
   * @param xPoints The x-coordinates of the points.
   * @param yPoints The y-coordinates of the points.
   */
  public Hitbox(double[] xPoints, double[] yPoints) {
    this.originalShape = new Polygon(xPoints, yPoints);
    this.shape = new Polygon(xPoints, yPoints);
  }

  /**
   * Returns the shape of the hitbox.
   *
   * @return The shape of the hitbox.
   */
  public Shape getShape() {
    return this.shape;
  }

  /**
   * Transforms the shape by scaling, translating, and rotating it.
   *
   * @param degrees    The angle in degrees to rotate the shape.
   * @param originX    The x-coordinate of the rotation origin.
   * @param originY    The y-coordinate of the rotation origin.
   * @param translateX The x-coordinate to translate the shape.
   * @param translateY The y-coordinate to translate the shape.
   * @param size       The size to scale the shape, where 100.0 represents the
   *                   original size.
   */
  public void translateAndRotateAndResize(
      double degrees,
      double originX,
      double originY,
      double translateX,
      double translateY,
      double size) {

    this.shape = this.originalShape.scale(size / 100.0, size / 100.0);
    this.shape = this.shape.translate(translateX, translateY);
    this.shape = this.shape.rotate(degrees, originX, originY);
  }

  private void drawDebug(PGraphics buffer, double r, double g, double b) {
    buffer.push();
    buffer.stroke((float) r, (float) g, (float) b);
    buffer.strokeWeight(2);
    buffer.noFill();
    drawShape(buffer);
    buffer.pop();
  }

  /**
   * Draws the hitbox with a debug color.
   *
   * @param buffer The buffer to draw the hitbox on.
   */
  public void drawDebug(PGraphics buffer) {
    this.drawDebug(buffer, Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
  }

  /**
   * Checks if the hitbox contains a point.
   *
   * @param x The x-coordinate of the point.
   * @param y The y-coordinate of the point.
   * @return {@code true} if the hitbox contains the point, {@code false}
   *         otherwise.
   */
  public boolean contains(double x, double y) {
    return this.shape.contains(x, y);
  }

  public Bounds getBounds() {
    return this.shape.getBounds();
  }

  /**
   * Checks if the hitbox intersects with another hitbox.
   *
   * @param hitbox The hitbox to check for intersection.
   * @return {@code true} if the hitboxes intersect, {@code false} otherwise.
   */
  public boolean intersects(Hitbox hitbox) {
    if (this.getShape().getBounds().intersects(hitbox.getShape().getBounds())) {

      return this.getShape().intersects(hitbox.getShape());
    }
    return false;
  }

  /**
   * Draws the shape.
   *
   * @param buffer The buffer to draw the shape on.
   */
  public void drawShape(PGraphics buffer) {
    if (shape == null) {
      return;
    }
    this.shape.draw(buffer);
  }
}
