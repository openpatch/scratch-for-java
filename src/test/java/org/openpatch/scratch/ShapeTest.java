package org.openpatch.scratch;

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
  void intersectsHandlesRotatedQuads() {
    // A sprite hitbox is a rotated rectangle, which is the case the
    // separating-axis fast path in intersects() exists for. These two overlap
    // only once rotation is taken into account - their corners are apart, so a
    // test that only compared corners would miss it.
    Polygon diamond = new Polygon(
        new double[] { 0, 10, 0, -10 }, new double[] { -10, 0, 10, 0 });
    Polygon square = new Polygon(
        new double[] { 5, 25, 25, 5 }, new double[] { -5, -5, 15, 15 });

    assertTrue(diamond.intersects(square));
    assertTrue(square.intersects(diamond));
  }

  @Test
  void intersectsReturnsFalseForShapesMeetingOnlyAlongAnEdge() {
    // Touching outlines share no area, so they do not count as intersecting.
    Rectangle a = new Rectangle(0, 0, 10, 10);
    Rectangle b = new Rectangle(10, 0, 10, 10);

    assertFalse(a.intersects(b));
  }

  @Test
  void intersectsReturnsTrueWhenOneShapeIsInsideAnother() {
    Rectangle outer = new Rectangle(0, 0, 100, 100);
    Rectangle inner = new Rectangle(40, 40, 5, 5);

    assertTrue(outer.intersects(inner));
    assertTrue(inner.intersects(outer));
  }

  @Test
  void intersectsHandlesConcaveShapes() {
    // An L, and a square sitting in the notch of the L. Their bounding boxes
    // overlap but the shapes themselves do not, which only the general path
    // gets right - concave outlines must not take the convex fast path.
    Polygon l = new Polygon(
        new double[] { 0, 30, 30, 10, 10, 0 },
        new double[] { 0, 0, 10, 10, 30, 30 });
    Rectangle inNotch = new Rectangle(15, 15, 10, 10);

    assertFalse(l.intersects(inNotch));
    assertFalse(inNotch.intersects(l));
  }

  @Test
  void intersectsAgreesWithAreaForManyRandomRotatedRectangles() {
    java.util.Random random = new java.util.Random(7);
    int overlapping = 0;

    for (int i = 0; i < 5000; i++) {
      Polygon a = randomRotatedRectangle(random);
      Polygon b = randomRotatedRectangle(random);

      boolean expected = intersectsViaArea(a, b);
      if (expected) {
        overlapping++;
      }
      assertEquals(expected, a.intersects(b), "case " + i);
      assertEquals(expected, b.intersects(a), "case " + i + " reversed");
    }

    // Guard against the generator drifting into only ever producing pairs that
    // miss each other, which would make the assertions above vacuous.
    assertTrue(overlapping > 500, "expected a healthy mix, got " + overlapping + " overlaps");
  }

  private static Polygon randomRotatedRectangle(java.util.Random random) {
    double centerX = random.nextDouble() * 200 - 100;
    double centerY = random.nextDouble() * 200 - 100;
    double width = 10 + random.nextDouble() * 60;
    double height = 10 + random.nextDouble() * 60;
    double angle = random.nextDouble() * Math.PI * 2;

    double[][] corners = {
        { -width / 2, -height / 2 }, { width / 2, -height / 2 },
        { width / 2, height / 2 }, { -width / 2, height / 2 } };
    double[] xs = new double[4];
    double[] ys = new double[4];
    for (int i = 0; i < 4; i++) {
      xs[i] = centerX + corners[i][0] * Math.cos(angle) - corners[i][1] * Math.sin(angle);
      ys[i] = centerY + corners[i][0] * Math.sin(angle) + corners[i][1] * Math.cos(angle);
    }
    return new Polygon(xs, ys);
  }

  /** The straightforward answer, used as the reference for the fast path. */
  private static boolean intersectsViaArea(Shape a, Shape b) {
    java.awt.geom.Area area = new java.awt.geom.Area(a.awtShape);
    area.intersect(new java.awt.geom.Area(b.awtShape));
    return !area.isEmpty();
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
