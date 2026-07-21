package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Bounds;

class HitboxTest {

  private static final double DELTA = 1e-9;

  private static Hitbox square(double x, double y, double size) {
    return new Hitbox(
        new double[] {x, x + size, x + size, x},
        new double[] {y, y, y + size, y + size});
  }

  @Test
  void containsPointsInsideTheOriginalShape() {
    Hitbox hitbox = square(0, 0, 10);

    assertTrue(hitbox.contains(5, 5));
    assertFalse(hitbox.contains(15, 15));
  }

  @Test
  void translateAndRotateAndResizeAppliesScaleThenTranslateThenRotate() {
    Hitbox hitbox = square(0, 0, 10);

    // scale(200%) -> 0..20 square, translate(5,5) -> 5..25 square, rotate(0)
    // is a no-op, so the resulting bounds should be a 20x20 square at (5,5).
    hitbox.translateAndRotateAndResize(0, 0, 0, 5, 5, 200);

    Bounds bounds = hitbox.getBounds();
    assertEquals(5.0, bounds.x(), DELTA);
    assertEquals(5.0, bounds.y(), DELTA);
    assertEquals(20.0, bounds.width(), DELTA);
    assertEquals(20.0, bounds.height(), DELTA);
    assertTrue(hitbox.contains(15, 15));
    assertFalse(hitbox.contains(0, 0));
  }

  @Test
  void translateAndRotateAndResizeAppliesRotationAroundOrigin() {
    Hitbox hitbox = square(0, 0, 10);

    // No scale, no translate, but a 90 degree rotation around the origin:
    // (0,0),(10,0),(10,10),(0,10) -> (0,0),(0,10),(-10,10),(-10,0).
    hitbox.translateAndRotateAndResize(90, 0, 0, 0, 0, 100);

    Bounds bounds = hitbox.getBounds();
    assertEquals(-10.0, bounds.x(), DELTA);
    assertEquals(0.0, bounds.y(), DELTA);
    assertEquals(10.0, bounds.width(), DELTA);
    assertEquals(10.0, bounds.height(), DELTA);
  }

  @Test
  void intersectsReturnsTrueForOverlappingHitboxes() {
    Hitbox a = square(0, 0, 10);
    Hitbox b = square(5, 5, 10);

    assertTrue(a.intersects(b));
    assertTrue(b.intersects(a));
  }

  @Test
  void intersectsReturnsFalseForDisjointHitboxes() {
    Hitbox a = square(0, 0, 10);
    Hitbox b = square(100, 100, 10);

    assertFalse(a.intersects(b));
  }

  @Test
  void translateAndRotateAndResizeAlwaysTransformsFromTheOriginalShape() {
    // Each call must re-derive from the untouched original shape rather than
    // compounding onto whatever the previous call left behind.
    Hitbox hitbox = square(0, 0, 10);

    hitbox.translateAndRotateAndResize(0, 0, 0, 50, 50, 100);
    hitbox.translateAndRotateAndResize(0, 0, 0, 5, 5, 100);

    Bounds bounds = hitbox.getBounds();
    assertEquals(5.0, bounds.x(), DELTA);
    assertEquals(5.0, bounds.y(), DELTA);
    assertEquals(10.0, bounds.width(), DELTA);
    assertEquals(10.0, bounds.height(), DELTA);
  }
}
