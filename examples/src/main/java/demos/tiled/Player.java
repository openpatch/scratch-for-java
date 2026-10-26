package demos.tiled;

import org.openpatch.scratch.*;
import org.openpatch.scratch.*;

public class Player extends Sprite {

  private String state = "idle";
  private double speed = 2;
  private String dir = "down";

  public Player(double x, double y) {
    this.setX(x);
    this.setY(y);

    // begin sprite editor
    this.addAnimation("walk-down", "demos/tiled/assets/Skeleton.png", 32, 32, 0, 4, 8, 12); // column 0
    this.addAnimation("walk-up", "demos/tiled/assets/Skeleton.png", 32, 32, 1, 5, 9, 13); // column 1
    this.addAnimation("walk-left", "demos/tiled/assets/Skeleton.png", 32, 32, 2, 6, 10, 14); // column 2
    this.addAnimation("walk-right", "demos/tiled/assets/Skeleton.png", 32, 32, 3, 7, 11, 15); // column 3

    this.setHitbox(new Polygon(new double[] {1, 1, 30, 30}, new double[] {31, 10, 10, 31}));
    // end sprite editor
  }

  public boolean hasItem(String name) {
    return GameState.hasItem(name);
  }

  public void tryMove(double dx, double dy) {
    this.changeX(dx);
    this.changeY(dy);
    if (!hasItem("scroll-water") && this.isTouchingSprite(Water.class)) {
      this.think(I18n.get("need-scroll-water"), 5);
      this.changeX(-dx);
      this.changeY(-dy);
    }
    if (this.isTouchingSprite(Wall.class)) {
      this.changeX(-dx);
      this.changeY(-dy);
    }
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (hasItem("scroll-fire") && keyCode == KeyCode.G) {
      var f = new Fireball(this.getX(), this.getY(), dir);
      this.getStage().add(f);
    }
  }

  public void run() {
    state = "idle";

    var dx = 0.0;
    var dy = 0.0;

    if (this.isKeyPressed(KeyCode.W) || this.isKeyPressed(KeyCode.UP)) {
      dy += this.speed;
      state = "walk";
      dir = "up";
    }

    if (this.isKeyPressed(KeyCode.S) || this.isKeyPressed(KeyCode.DOWN)) {
      dy -= this.speed;
      state = "walk";
      dir = "down";
    }

    if (this.isKeyPressed(KeyCode.A) || this.isKeyPressed(KeyCode.LEFT)) {
      dx -= this.speed;
      state = "walk";
      dir = "left";
    }

    if (this.isKeyPressed(KeyCode.D) || this.isKeyPressed(KeyCode.RIGHT)) {
      dx += this.speed;
      state = "walk";
      dir = "right";
    }

    var item = this.getTouchingSprite(Item.class);
    if (item != null && this.isKeyPressed(KeyCode.E)) {
      item.collect();
    } else if (item != null) {
      this.getStage().display(I18n.get("tutorial-item-pickup"), 1);
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
