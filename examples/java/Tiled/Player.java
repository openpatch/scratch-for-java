package Tiled;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;

public class Player extends AnimatedSprite {

  private String state = "idle";
  private double speed = 2.5;
  private String dir = "down";
  private double mapX = 0;
  private double mapY = 0;

  public Player(double mapX, double mapY) {
    this.mapX = mapX;
    this.mapY = mapY;

    this.addAnimation("walk-down", "Tiled/assets/Skeleton.png", 4, 32, 32, 0, true);
    this.addAnimation("walk-up", "Tiled/assets/Skeleton.png", 4, 32, 32, 1, true);
    this.addAnimation("walk-left", "Tiled/assets/Skeleton.png", 4, 32, 32, 2, true);
    this.addAnimation("walk-right", "Tiled/assets/Skeleton.png", 4, 32, 32, 3, true);

    this.setHitbox(1, 31, 1, 10, 30, 10, 30, 31);
  }

  public void setMapX(double mapX) {
    this.mapX = mapX;
    this.setX(this.mapX - GameState.get().camX);
  }

  public void changeMapX(double dx) {
    this.setMapX(this.mapX + dx);
  }

  public void setMapY(double mapY) {
    this.mapY = mapY;
    this.setY(this.mapY - GameState.get().camY);
  }

  public void changeMapY(double dy) {
    this.setMapY(this.mapY + dy);
  }

  public boolean hasItem(String name) {
    return GameState.get().items.contains(name);
  }

  public void tryMove(double dx, double dy) {
    this.changeMapX(dx);
    this.changeMapY(dy);
    if (!hasItem("scroll-water") && this.isTouchingSprite(Water.class)) {
      this.think(I18n.get("need-scroll-water"), 2000);
      this.changeMapX(-dx);
      this.changeMapY(-dy);
    }
    if (this.isTouchingSprite(Wall.class)) {
      this.changeMapX(-dx);
      this.changeMapY(-dy);
    }
  }

  public void whenKeyPressed(int keyCode) {
    if (hasItem("scroll-fire") && keyCode == KeyCode.VK_G) {
      var f = new Fireball(mapX, mapY, dir);
      this.getStage().add(f);
    }
  }

  public void run() {
    state = "idle";

    var dx = 0;
    var dy = 0;

    if (this.isKeyPressed(KeyCode.VK_W) || this.isKeyPressed(KeyCode.VK_UP)) {
      dy += this.speed;
      state = "walk";
      dir = "up";
    }

    if (this.isKeyPressed(KeyCode.VK_S) || this.isKeyPressed(KeyCode.VK_DOWN)) {
      dy -= this.speed;
      state = "walk";
      dir = "down";
    }

    if (this.isKeyPressed(KeyCode.VK_A) || this.isKeyPressed(KeyCode.VK_LEFT)) {
      dx -= this.speed;
      state = "walk";
      dir = "left";
    }

    if (this.isKeyPressed(KeyCode.VK_D) || this.isKeyPressed(KeyCode.VK_RIGHT)) {
      dx += this.speed;
      state = "walk";
      dir = "right";
    }

    var item = this.getTouchingSprite(Item.class);
    if (item != null && this.isKeyPressed(KeyCode.VK_E)) {
      item.collect();
    } else if (item != null) {
      this.getStage().display(I18n.get("tutorial-item-pickup"), 1000);
    }

    if (state.equals("idle")) {
      this.resetAnimation();
    }

    this.playAnimation("walk-" + dir);

    this.tryMove(dx, 0);
    this.tryMove(0, dy);

    GameState.get().camX = this.mapX;
    GameState.get().camY = this.mapY;
  }
}
