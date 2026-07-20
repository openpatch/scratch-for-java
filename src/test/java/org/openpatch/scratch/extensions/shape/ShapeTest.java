package org.openpatch.scratch.extensions.shape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ShapeTest {

  private static final double DELTA = 1e-9;

  @Test
  void rectangleContainsPointsInsideItsBounds() {
    Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    assertTrue(rectangle.contains(5, 5));
    assertFalse(rectangle.contains(15, 15));
  }

  @Test
  void circleContainsPointsWithinRadius() {
    Circle circle = new Circle(0, 0, 5);

    assertTrue(circle.contains(0, 0));
    assertTrue(circle.contains(4, 0));
    assertFalse(circle.contains(10, 0));
  }

  @Test
  void polygonBehavesLikeARectangleWhenGivenSquareCorners() {
    Polygon square = new Polygon(new double[] {0, 10, 10, 0}, new double[] {0, 0, 10, 10});

    assertTrue(square.contains(5, 5));
    assertFalse(square.contains(15, 15));
  }

  @Test
  void polygonAddPointRebuildsTheShape() {
    Polygon polygon = new Polygon();
    polygon.addPoint(0, 0);
    polygon.addPoint(10, 0);
    polygon.addPoint(10, 10);
    polygon.addPoint(0, 10);

    assertTrue(polygon.contains(5, 5));
  }

  @Test
  void triangleContainsPointsInsideItsVertices() {
    Triangle triangle = new Triangle(0, 0, 10, 0, 5, 10);

    assertTrue(triangle.contains(5, 3));
    assertFalse(triangle.contains(9, 9));
  }

  @Test
  void intersectsReturnsTrueForOverlappingShapes() {
    Rectangle a = new Rectangle(0, 0, 10, 10);
    Rectangle b = new Rectangle(5, 5, 10, 10);

    assertTrue(a.intersects(b));
  }

  @Test
  void intersectsReturnsFalseForDisjointShapes() {
    Rectangle a = new Rectangle(0, 0, 10, 10);
    Rectangle b = new Rectangle(20, 20, 5, 5);

    assertFalse(a.intersects(b));
  }

  @Test
  void scaleGrowsBoundsAroundTheOrigin() {
    Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    Bounds bounds = rectangle.scale(2, 2).getBounds();

    assertEquals(new Bounds(0, 0, 20, 20), bounds);
  }

  @Test
  void translateShiftsBoundsByTheGivenDistance() {
    Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    Bounds bounds = rectangle.translate(5, 5).getBounds();

    assertEquals(new Bounds(5, 5, 10, 10), bounds);
  }

  @Test
  void rotate90DegreesAroundOriginRotatesTheBounds() {
    Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    // Corners (0,0),(10,0),(10,10),(0,10) rotated 90 degrees around the
    // origin map to (0,0),(0,10),(-10,10),(-10,0).
    Bounds bounds = rectangle.rotate(90, 0, 0).getBounds();

    assertEquals(-10.0, bounds.x(), DELTA);
    assertEquals(0.0, bounds.y(), DELTA);
    assertEquals(10.0, bounds.width(), DELTA);
    assertEquals(10.0, bounds.height(), DELTA);
  }

  @Test
  void scaleTranslateAndRotateComposeInOrder() {
    Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    Bounds bounds = rectangle.scale(2, 2).translate(5, 5).getBounds();

    assertEquals(new Bounds(5, 5, 20, 20), bounds);
  }
}
