package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Vector2;

class SpriteTest {

  private static final double DELTA = 1e-9;

  @Test
  void newSpriteStartsAtOriginFacingRight() {
    Sprite sprite = new Sprite();

    assertEquals(0.0, sprite.getX(), DELTA);
    assertEquals(0.0, sprite.getY(), DELTA);
    assertEquals(90.0, sprite.getDirection(), DELTA);
  }

  @Test
  void moveAdvancesAlongTheCurrentDirection() {
    // Direction 90 (the default, facing right) moves along +x.
    Sprite sprite = new Sprite();
    sprite.move(10);

    assertEquals(10.0, sprite.getX(), DELTA);
    assertEquals(0.0, sprite.getY(), DELTA);
  }

  @Test
  void moveFacingUpAdvancesAlongPositiveY() {
    // Direction 0 faces up, so moving advances along +y.
    Sprite sprite = new Sprite();
    sprite.setDirection(0);
    sprite.move(10);

    assertEquals(0.0, sprite.getX(), DELTA);
    assertEquals(10.0, sprite.getY(), DELTA);
  }

  @Test
  void setDirectionNormalizesAboveUpperBound() {
    Sprite sprite = new Sprite();
    sprite.setDirection(370);

    assertEquals(10.0, sprite.getDirection(), DELTA);
  }

  @Test
  void setDirectionNormalizesNegativeValues() {
    Sprite sprite = new Sprite();
    sprite.setDirection(-30);

    assertEquals(330.0, sprite.getDirection(), DELTA);
  }

  @Test
  void setDirectionWrapsExactly360ToZero() {
    Sprite sprite = new Sprite();
    sprite.setDirection(360);

    assertEquals(0.0, sprite.getDirection(), DELTA);
  }

  @Test
  void turnRightIncreasesDirection() {
    Sprite sprite = new Sprite();
    sprite.turnRight(30);

    assertEquals(120.0, sprite.getDirection(), DELTA);
  }

  @Test
  void turnLeftDecreasesDirectionAndWraps() {
    Sprite sprite = new Sprite();
    sprite.turnLeft(100);

    assertEquals(350.0, sprite.getDirection(), DELTA);
  }

  @Test
  void setPositionUpdatesBothCoordinates() {
    Sprite sprite = new Sprite();
    sprite.setPosition(5, 10);

    assertEquals(5.0, sprite.getX(), DELTA);
    assertEquals(10.0, sprite.getY(), DELTA);
    assertEquals(new Vector2(5, 10), sprite.getPosition());
  }

  @Test
  void changePositionAddsTheGivenVector() {
    Sprite sprite = new Sprite();
    sprite.setPosition(5, 10);
    sprite.changePosition(new Vector2(2, 3));

    assertEquals(7.0, sprite.getX(), DELTA);
    assertEquals(13.0, sprite.getY(), DELTA);
  }

  @Test
  void setXAndChangeXOnlyAffectX() {
    Sprite sprite = new Sprite();
    sprite.setX(5);
    sprite.changeX(3);

    assertEquals(8.0, sprite.getX(), DELTA);
    assertEquals(0.0, sprite.getY(), DELTA);
  }

  @Test
  void setYAndChangeYOnlyAffectY() {
    Sprite sprite = new Sprite();
    sprite.setY(5);
    sprite.changeY(3);

    assertEquals(0.0, sprite.getX(), DELTA);
    assertEquals(8.0, sprite.getY(), DELTA);
  }

  @Test
  void distanceToSpriteComputesEuclideanDistance() {
    Sprite a = new Sprite();
    Sprite b = new Sprite();
    a.setPosition(0, 0);
    b.setPosition(3, 4);

    assertEquals(5.0, a.distanceToSprite(b), DELTA);
  }

  @Test
  void ifOnEdgeBounceIsANoOpWhenNotAddedToAStage() {
    // Regression guard: this used to throw a NullPointerException, since it
    // read stage border fields without checking whether the sprite had a
    // stage at all.
    Sprite sprite = new Sprite();
    sprite.setPosition(1000, 1000);

    sprite.ifOnEdgeBounce();

    assertEquals(1000.0, sprite.getX(), DELTA);
    assertEquals(1000.0, sprite.getY(), DELTA);
  }

  @Test
  void hitboxFollowsTheSpriteAfterItMoves() {
    // getHitbox() reuses the last hitbox while the sprite stands still, so that
    // collision checks do not rebuild it once per pair. Anything that changes
    // the shape has to be noticed, or sprites collide where they used to be.
    Sprite sprite = new Sprite();
    sprite.setPosition(0, 0);
    double startX = sprite.getHitbox().getBounds().x();

    sprite.setPosition(100, 0);

    assertEquals(startX + 100, sprite.getHitbox().getBounds().x(), DELTA);
  }

  @Test
  void customHitboxFollowsTheSpriteAfterItIsResized() {
    Sprite sprite = new Sprite();
    sprite.setHitbox(0, 0, 40, 0, 40, 20, 0, 20);
    double startWidth = sprite.getHitbox().getBounds().width();

    sprite.setSize(200);

    assertEquals(2 * startWidth, sprite.getHitbox().getBounds().width(), DELTA);
  }

  @Test
  void customHitboxFollowsTheSpriteAfterItTurns() {
    Sprite sprite = new Sprite();
    sprite.setHitbox(0, 0, 40, 0, 40, 20, 0, 20);
    double startWidth = sprite.getHitbox().getBounds().width();

    sprite.setDirection(180);

    // A quarter turn stands the box on its end, so what was its width is now
    // its height.
    assertEquals(startWidth, sprite.getHitbox().getBounds().height(), DELTA);
  }
}
