package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Image;

import processing.core.PConstants;
import processing.core.PImage;

/**
 * A sprite with rotation style LEFT_RIGHT is drawn mirrored when it faces left,
 * so its hitbox has to be mirrored with it: otherwise a costume whose painted
 * part is not in the middle of its canvas collides with empty space.
 */
class HitboxMirrorTest {

  private static final double DELTA = 1e-6;

  /**
   * A sprite wearing a 10 x 4 costume of which only the two leftmost columns are
   * painted, standing at the origin.
   */
  private static Sprite spritePaintedOnTheLeft() throws Exception {
    PImage picture = new PImage(10, 4, PConstants.ARGB);
    picture.loadPixels();
    for (int y = 0; y < 4; y++) {
      picture.pixels[y * 10] = 0xff000000;
      picture.pixels[y * 10 + 1] = 0xff000000;
    }
    picture.updatePixels();

    Sprite sprite = new Sprite();
    Field costumes = Sprite.class.getDeclaredField("costumes");
    costumes.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<Image> list = (List<Image>) costumes.get(sprite);
    list.add(new Image("left", picture));
    sprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
    return sprite;
  }

  @Test
  void facingRightTheHitboxCoversThePaintedColumns() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(-5, bounds.x(), DELTA);
    assertEquals(2, bounds.width(), DELTA);
  }

  @Test
  void facingLeftTheHitboxIsMirroredLikeTheCostume() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setDirection(-90);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(3, bounds.x(), DELTA);
    assertEquals(2, bounds.width(), DELTA);
  }

  @Test
  void turningLeftAfterTheHitboxWasAskedForMirrorsIt() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.getHitbox();
    sprite.setDirection(-90);

    assertEquals(3, sprite.getHitbox().getBounds().x(), DELTA);
  }

  @Test
  void aHitboxOfYourOwnIsMirroredAsWell() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    // just the painted columns, in the costume's own pixels
    sprite.setHitbox(0, 0, 2, 0, 2, 4, 0, 4);
    sprite.setDirection(-90);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(3, bounds.x(), DELTA);
    assertEquals(2, bounds.width(), DELTA);
  }

  @Test
  void turningAllAroundIsLeftToTheRotation() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setRotationStyle(RotationStyle.DONT);
    sprite.setDirection(-90);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(-5, bounds.x(), DELTA);
  }

  // ---- rotation center ----

  @Test
  void theRotationCenterIsThePointAtTheSpritesPosition() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setRotationCenter(0, 0); // the top left corner

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(0, bounds.x(), DELTA);
    assertEquals(0, bounds.y(), DELTA);
    assertEquals(2, bounds.width(), DELTA);
  }

  @Test
  void theRotationCenterGrowsWithTheSprite() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setRotationCenter(10, 4); // the bottom right corner
    sprite.setSize(200);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(-20, bounds.x(), DELTA);
    assertEquals(-8, bounds.y(), DELTA);
    assertEquals(4, bounds.width(), DELTA);
  }

  @Test
  void aSpriteFacingLeftIsMirroredAboutItsRotationCenter() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setRotationCenter(0, 0);
    sprite.setDirection(-90);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(-2, bounds.x(), DELTA);
    assertEquals(2, bounds.width(), DELTA);
  }

  @Test
  void aHitboxOfYourOwnFollowsTheRotationCenter() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setHitbox(0, 0, 2, 0, 2, 4, 0, 4);
    sprite.setRotationCenter(0, 0);

    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(0, bounds.x(), DELTA);
    assertEquals(0, bounds.y(), DELTA);
  }

  @Test
  void aSpriteTurnsAboutItsRotationCenter() throws Exception {
    Sprite sprite = spritePaintedOnTheLeft();
    sprite.setRotationStyle(RotationStyle.ALL_AROUND);
    sprite.setRotationCenter(0, 0);
    sprite.setDirection(180); // a quarter turn clockwise

    // the 2 x 4 painted columns hang down from the corner, now lying to its left
    Bounds bounds = sprite.getHitbox().getBounds();
    assertEquals(-4, bounds.x(), DELTA);
    assertEquals(0, bounds.y(), DELTA);
    assertEquals(4, bounds.width(), DELTA);
    assertEquals(2, bounds.height(), DELTA);
  }
}
