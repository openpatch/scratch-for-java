package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoundsTest {

  @Test
  void intersectsReturnsTrueForOverlappingBounds() {
    Bounds a = new Bounds(0, 0, 10, 10);
    Bounds b = new Bounds(5, 5, 10, 10);

    assertTrue(a.intersects(b));
    assertTrue(b.intersects(a));
  }

  @Test
  void intersectsReturnsFalseForDisjointBounds() {
    Bounds a = new Bounds(0, 0, 10, 10);
    Bounds b = new Bounds(20, 20, 5, 5);

    assertFalse(a.intersects(b));
  }

  @Test
  void intersectsReturnsFalseForBoundsThatOnlyTouchAtTheEdge() {
    Bounds a = new Bounds(0, 0, 10, 10);
    Bounds b = new Bounds(10, 0, 10, 10);

    assertFalse(a.intersects(b));
  }

  @Test
  void intersectsReturnsTrueWhenOneBoundsContainsTheOther() {
    Bounds outer = new Bounds(0, 0, 10, 10);
    Bounds inner = new Bounds(2, 2, 4, 4);

    assertTrue(outer.intersects(inner));
    assertTrue(inner.intersects(outer));
  }
}
