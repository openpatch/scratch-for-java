package org.openpatch.scratch.internal.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Circle;
import org.openpatch.scratch.Polygon;
import org.openpatch.scratch.Rectangle;
import org.openpatch.scratch.Shape;

class GeometryTest {

  /** An L: the 10×10 square from (0, 0) without its top-right 5×5 quarter. */
  static Polygon ell() {
    return new Polygon(new double[] {0, 10, 10, 5, 5, 0}, new double[] {0, 0, 5, 5, 10, 10});
  }

  @Test
  void squaresSharingAnEdgeDoNotIntersect() {
    assertFalse(new Rectangle(0, 0, 10, 10).intersects(new Rectangle(10, 0, 10, 10)));
  }

  @Test
  void squaresSharingACornerDoNotIntersect() {
    assertFalse(new Rectangle(0, 0, 10, 10).intersects(new Rectangle(10, 10, 10, 10)));
  }

  @Test
  void overlappingAndIdenticalSquaresIntersect() {
    assertTrue(new Rectangle(0, 0, 10, 10).intersects(new Rectangle(9.5, 9.5, 10, 10)));
    assertTrue(new Rectangle(0, 0, 10, 10).intersects(new Rectangle(0, 0, 10, 10)));
  }

  @Test
  void aSquareInTheNotchOfAnLDoesNotIntersectIt() {
    assertFalse(ell().intersects(new Rectangle(6, 6, 3, 3)));
    assertFalse(new Rectangle(6, 6, 3, 3).intersects(ell()));
  }

  @Test
  void aSquarePushedIntoTheArmOfAnLIntersectsIt() {
    assertTrue(ell().intersects(new Rectangle(6, 4.5, 3, 3)));
  }

  @Test
  void aSquareLyingOnTheEdgeOfTheNotchDoesNotIntersect() {
    assertFalse(ell().intersects(new Rectangle(6, 5, 3, 3)));
    assertFalse(ell().intersects(new Rectangle(5, 6, 3, 3)));
  }

  @Test
  void aCircleTouchingASquareAtOnePointDoesNotIntersect() {
    // the flattened circle has a corner at (15, 5)
    assertFalse(new Circle(20, 5, 5).intersects(new Rectangle(5, 0, 10, 10)));
    assertTrue(new Circle(19, 5, 5).intersects(new Rectangle(5, 0, 10, 10)));
  }

  @Test
  void trianglesCoverAConcavePolygonInEitherWinding() {
    double[] ring = ell().getPoints();
    double[] reversed = new double[ring.length];
    for (int i = 0; i < ring.length; i += 2) {
      reversed[ring.length - 2 - i] = ring[i];
      reversed[ring.length - 1 - i] = ring[i + 1];
    }
    for (double[] r : new double[][] {ring, reversed}) {
      double area = 0;
      for (double[] t : Geometry.triangulate(r)) {
        area += Math.abs(Geometry.signedArea(t));
      }
      assertEquals(75, area, 1e-9);
    }
  }

  @Test
  void ellipsesStayWithinAQuarterPixel() {
    for (double r : new double[] {5, 100, 1000}) {
      double[] ring = Geometry.ellipse(0, 0, r, r);
      int n = ring.length / 2;
      // the worst point of a chord is its middle
      double mx = (ring[0] + ring[2]) / 2, my = (ring[1] + ring[3]) / 2;
      assertTrue(r - Math.hypot(mx, my) <= 0.25 + 1e-9, "radius " + r + " with " + n + " segments");
    }
  }

  @Test
  void rotatingAQuarterTurnAboutAPoint() {
    Shape s = new Rectangle(10, 0, 2, 1).rotate(90, 0, 0);
    assertEquals(-1, s.getBounds().x(), 1e-9);
    assertEquals(10, s.getBounds().y(), 1e-9);
    assertEquals(1, s.getBounds().width(), 1e-9);
    assertEquals(2, s.getBounds().height(), 1e-9);
  }

  @Test
  void mirroringFlipsTheBounds() {
    Shape s = new Rectangle(2, 0, 3, 1).scale(-1, 1);
    assertEquals(-5, s.getBounds().x(), 1e-9);
    assertEquals(3, s.getBounds().width(), 1e-9);
  }

  @Test
  void theNotchOfAnLIsOutside() {
    assertFalse(ell().contains(7, 7));
    assertTrue(ell().contains(2, 8));
    assertTrue(ell().contains(8, 2));
  }

  @Test
  void convexityIgnoresStraightPoints() {
    assertTrue(Geometry.isConvex(new double[] {0, 0, 5, 0, 10, 0, 10, 10, 0, 10}));
    assertFalse(Geometry.isConvex(ell().getPoints()));
  }

  @Test
  void ellipseBoundsAreExact() {
    var b = new org.openpatch.scratch.Ellipse(0, 0, 10, 20).getBounds();
    assertEquals(0, b.x(), 1e-9);
    assertEquals(0, b.y(), 1e-9);
    assertEquals(10, b.width(), 1e-9);
    assertEquals(20, b.height(), 1e-9);
  }
}
