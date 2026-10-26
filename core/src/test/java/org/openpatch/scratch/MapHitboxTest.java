package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;

/**
 * A hitbox without a costume, as a map makes one: the sprite sits where its object is, and the
 * object's area is its hitbox (the tiled demo's walls, water and warps work this way).
 */
class MapHitboxTest {

  Game game;
  Stage stage;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(640, 360);
    this.stage = new Stage();
    this.game.setStage(this.stage);
  }

  static class Wall extends Sprite {
    Wall(double x, double y, double width, double height) {
      this.setX(x);
      this.setY(y);
      this.setHitbox(new Rectangle(0, 0, width, height));
    }
  }

  @Test
  void hitboxWithoutCostumeCoversTheObjectArea() {
    // a Tiled rectangle at (2, -4) with size 56 x 280: the object's y is the top edge,
    // so the stage position is (2, 4) and the area runs 280 units down from there
    var wall = new Wall(2, 4, 56, 280);
    this.stage.add(wall);

    Bounds bounds = wall.getHitbox().getBounds();

    assertEquals(2, bounds.x(), 1e-9);
    assertEquals(4 - 280, bounds.y(), 1e-9);
    assertEquals(56, bounds.width(), 1e-9);
    assertEquals(280, bounds.height(), 1e-9);
  }

  @Test
  void nothingWithoutCostumeAndHitbox() {
    var bare = new Sprite();
    this.stage.add(bare);

    assertNull(bare.getHitbox());
    assertNull(bare.touchHitbox());
  }

  @Test
  void spriteTouchesACostumelessWall() {
    var wall = new Wall(2, 4, 56, 280);
    this.stage.add(wall);

    var player = new Sprite("player", "costumes/red10.png");
    player.setPosition(20, -100); // inside the wall's area
    this.stage.add(player);

    assertTrue(player.isTouchingSprite(wall));
    assertTrue(player.isTouchingSprite(Wall.class));
    assertEquals(wall, player.getTouchingSprite(Wall.class));
  }

  @Test
  void spriteDoesNotTouchACostumelessWallItLeft() {
    var wall = new Wall(2, 4, 56, 280);
    this.stage.add(wall);

    var player = new Sprite("player", "costumes/red10.png");
    player.setPosition(200, 100); // outside the wall's area
    this.stage.add(player);

    assertFalse(player.isTouchingSprite(wall));
    assertFalse(player.isTouchingSprite(Wall.class));
    assertNull(player.getTouchingSprite(Wall.class));
  }

  @Test
  void framelessHitboxMovesWithTheSprite() {
    var wall = new Wall(2, 4, 56, 280);
    this.stage.add(wall);

    var player = new Sprite("player", "costumes/red10.png");
    player.setPosition(20, -100);
    this.stage.add(player);
    assertTrue(player.isTouchingSprite(wall));

    wall.setPosition(400, 300);

    assertFalse(player.isTouchingSprite(wall));
    assertEquals(400, wall.getHitbox().getBounds().x(), 1e-9);
    assertEquals(300 - 280, wall.getHitbox().getBounds().y(), 1e-9);
  }
}
