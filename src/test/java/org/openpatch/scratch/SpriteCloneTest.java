package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpriteCloneTest {

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

    Sprite copy = cat.clone();

    assertTrue(copy instanceof Cat, "a clone of a Cat is a Cat");
    Cat clone = (Cat) copy;
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
    Cat clone = (Cat) cat.clone();
    assertEquals(1, clone.starts);
    assertEquals(0, cat.starts);
    assertTrue(clone.isClone());
    assertFalse(cat.isClone());
    assertTrue(((Cat) clone.clone()).isClone(), "a clone of a clone is a clone");
  }

  @Test
  void aCloneHasItsOwnPenAndTimers() {
    Cat cat = new Cat();
    cat.getPen().setSize(5);
    Cat clone = (Cat) cat.clone();
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
  void anAnimatedSpriteCloneKeepsItsAnimations() {
    AnimatedSprite sprite = new AnimatedSprite();
    AnimatedSprite clone = sprite.clone();
    assertTrue(clone.isClone());
    assertNotSame(sprite, clone);
  }
}
