package demos.tiled;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;

public class Player extends AnimatedSprite {

  private String state = "idle";
  private double speed = 2.5;
  private String dir = "down";

  public Player(double x, double y) {
    this.setX(x);
    this.setY(y);

    this.addAnimation("walk-down", "demos/tiled/assets/Skeleton.png", 4, 32, 32, 0, true);
    this.addAnimation("walk-up", "demos/tiled/assets/Skeleton.png", 4, 32, 32, 1, true);
    this.addAnimation("walk-left", "demos/tiled/assets/Skeleton.png", 4, 32, 32, 2, true);
    this.addAnimation("walk-right", "demos/tiled/assets/Skeleton.png", 4, 32, 32, 3, true);

    this.setHitbox(1, 31, 1, 10, 30, 10, 30, 31);
  }

  public boolean hasItem(String name) {
    return GameState.get().items.contains(name);
  }

  public void tryMove(double dx, double dy) {
    this.changeX(dx);
    this.changeY(dy);
    if (!hasItem("scroll-water") && this.isTouchingSprite(Water.class)) {
      this.think(I18n.get("need-scroll-water"), 5000);
      this.changeX(-dx);
      this.changeY(-dy);
    }
    if (this.isTouchingSprite(Wall.class)) {
      this.changeX(-dx);
      this.changeY(-dy);
    }
  }

  public void whenKeyPressed(int keyCode) {
    if (hasItem("scroll-fire") && keyCode == KeyCode.VK_G) {
      var f = new Fireball(this.getX(), this.getY(), dir);
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

    GameState.get().playerX = this.getX();
    GameState.get().playerY = this.getY();

    this.tryMove(dx, 0);
    this.tryMove(0, dy);
  }
}
