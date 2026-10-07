package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Sprites with costumes, without a window: costumes load straight from the
 * built-in sheets, so sizes, costume switching, collision and clones can be
 * checked in plain unit tests.
 */
class HeadlessCostumeTest {

  @Test
  void aBuiltinCostumeLoadsWithoutAWindow() {
    Sprite slime = new Sprite("slime", "slimeGreen");
    assertTrue(slime.getWidth() > 0, "the costume has its real size");
    assertTrue(slime.getHeight() > 0);
    assertEquals("slime", slime.getCurrentCostumeName());
  }

  @Test
  void costumesSwitchAndSizeScalesTheCostume() {
    Sprite sprite = new Sprite();
    sprite.addCostume("small", "slimeGreen");
    sprite.addCostume("big", "bunny1_stand");
    assertEquals("small", sprite.getCurrentCostumeName());
    int width = sprite.getWidth();
    sprite.nextCostume();
    assertEquals("big", sprite.getCurrentCostumeName());
    assertNotEquals(width, sprite.getWidth());
    int full = sprite.getWidth();
    sprite.setSize(50);
    assertEquals(full / 2.0, sprite.getWidth(), 1);
  }

  @Test
  void hitboxesFollowTheCostumes() {
    // isTouchingSprite() also wants both on a stage; their hitboxes need none
    Sprite a = new Sprite("slime", "slimeGreen");
    Sprite b = new Sprite("slime", "slimeGreen");
    assertTrue(a.getHitbox().intersects(b.getHitbox()), "both in the middle");
    b.setX(a.getWidth() * 3);
    assertFalse(a.getHitbox().intersects(b.getHitbox()), "far apart");
  }

  @Test
  void aCloneHasTheCostumes() {
    Sprite slime = new Sprite("slime", "slimeGreen");
    slime.addCostume("bunny", "bunny1_stand");
    Sprite clone = slime.clone();
    assertEquals(slime.getWidth(), clone.getWidth());
    clone.switchCostume("bunny");
    assertEquals("slime", slime.getCurrentCostumeName(), "the original keeps its costume");
  }

  @Test
  void animationsLoadTheirFrames() {
    AnimatedSprite bunny = new AnimatedSprite();
    bunny.addAnimation("walk", "bunny1_walk%d", 2);
    bunny.playAnimation("walk");
    assertTrue(bunny.getWidth() > 0);
  }
}
