package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Costumes, animations and clones, without a window: costumes load straight
 * from the built-in sheets and sprites can be cloned headlessly, so sizes,
 * costume switching, collision, animation state and clone semantics can be
 * checked in plain unit tests.
 */
class CostumeAndCloneTest {

  // ---- from HeadlessCostumeTest ----

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
    double width = sprite.getWidth();
    sprite.nextCostume();
    assertEquals("big", sprite.getCurrentCostumeName());
    assertNotEquals(width, sprite.getWidth());
    double full = sprite.getWidth();
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
    Sprite clone = slime.createClone();
    assertEquals(slime.getWidth(), clone.getWidth());
    clone.switchCostume("bunny");
    assertEquals("slime", slime.getCurrentCostumeName(), "the original keeps its costume");
  }

  @Test
  void animationsLoadTheirFrames() {
    Sprite bunny = new Sprite();
    bunny.addAnimation("walk", "bunny1_walk%d", 2);
    bunny.playAnimation("walk");
    assertTrue(bunny.getWidth() > 0);
  }

  // ---- from SpriteCloneTest ----

  static class Cat extends Sprite {
    int lives = 3;
    String name = "Tom";
    List<String> toys = new ArrayList<>();
    int starts = 0;
    static int constructed = 0;

    Cat() {
      constructed++;
    }

    @Override
    public void whenStartsAsClone() {
      starts++;
    }
  }

  @Test
  void aCloneIsOfTheSameClassWithACopyOfItsVariables() {
    Cat cat = new Cat();
    cat.lives = 7;
    cat.setPosition(30, -20);
    cat.setDirection(45);
    int constructed = Cat.constructed;

    Cat clone = cat.createClone();

    assertEquals(Cat.class, clone.getClass(), "a clone of a Cat is a Cat");
    assertEquals(7, clone.lives);
    assertEquals("Tom", clone.name);
    assertEquals(30, clone.getX(), 1e-9);
    assertEquals(-20, clone.getY(), 1e-9);
    assertEquals(45, clone.getDirection(), 1e-9);
    assertEquals(constructed, Cat.constructed, "the constructor does not run again");
    clone.lives = 1;
    assertEquals(7, cat.lives, "numbers are the clone's own");
    assertSame(cat.toys, clone.toys, "objects are shared, as documented");
  }

  @Test
  void theCloneStartsAsAClone() {
    Cat cat = new Cat();
    Cat clone = cat.createClone();
    assertEquals(1, clone.starts);
    assertEquals(0, cat.starts);
    assertTrue(clone.isClone());
    assertFalse(cat.isClone());
    assertTrue(clone.createClone().isClone(), "a clone of a clone is a clone");
  }

  @Test
  void aCloneHasItsOwnPenAndTimers() {
    Cat cat = new Cat();
    cat.getPen().setSize(5);
    Cat clone = cat.createClone();
    assertNotSame(cat.getPen(), clone.getPen());
    assertEquals(5, clone.getPen().getSize(), 1e-9);
    assertNotSame(cat.getTimer(), clone.getTimer());
    // changing the clone's pen leaves the original's alone
    clone.getPen().setSize(9);
    assertEquals(5, cat.getPen().getSize(), 1e-9);
  }

  @Test
  void anOriginalIsNotDeletedAsAClone() {
    Cat cat = new Cat();
    cat.deleteThisClone();
    assertFalse(cat.isClone());
  }

  @Test
  void aCloneOfASpriteWithAnimationsIsAClone() {
    Sprite sprite = new Sprite();
    Sprite clone = sprite.createClone();
    assertTrue(clone.isClone());
    assertNotSame(sprite, clone);
  }

  // ---- from AnimatedSpriteTest ----

  @Test
  void constructingASpriteWithoutAWindowHasNoAnimationPlayed() {
    Sprite sprite = new Sprite();

    assertEquals(0, sprite.getAnimationFrame());
    assertFalse(sprite.isAnimationPlayed());
  }

  @Test
  void animationIntervalDefaultsAndCanBeChanged() {
    Sprite sprite = new Sprite();

    assertEquals(0.12, sprite.getAnimationInterval(), 1e-9);

    sprite.setAnimationInterval(0.2);
    assertEquals(0.2, sprite.getAnimationInterval(), 1e-9);
  }

  @Test
  void setAnimationFrameUpdatesTheCurrentFrame() {
    Sprite sprite = new Sprite();
    sprite.setAnimationFrame(3);

    assertEquals(3, sprite.getAnimationFrame());
  }

  @Test
  void resetAnimationRestartsFromTheFirstFrame() {
    Sprite sprite = new Sprite();
    sprite.setAnimationFrame(5);

    sprite.resetAnimation();

    assertEquals(0, sprite.getAnimationFrame());
    assertFalse(sprite.isAnimationPlayed());
  }
}
