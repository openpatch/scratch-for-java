package Tiled;

public class Bamboo extends Enemy {

  protected double speed = 2.5;

  public Bamboo(double mapX, double mapY) {
    super(mapX, mapY);

    this.addAnimation("walk-down", "Tiled/assets/Bamboo.png", 4, 32, 32, 0, true);
    this.addAnimation("walk-up", "Tiled/assets/Bamboo.png", 4, 32, 32, 1, true);
    this.addAnimation("walk-left", "Tiled/assets/Bamboo.png", 4, 32, 32, 2, true);
    this.addAnimation("walk-right", "Tiled/assets/Bamboo.png", 4, 32, 32, 3, true);
  }

  public void run() {
    state = "walk";

    this.changeMapX(this.speed);

    if (this.speed > 0) {
      dir = "right";
    } else {
      dir = "left";
    }
    this.changeMapY(0);
    if (this.isTouchingSprite(Wall.class)) {
      state = "idle";
      this.changeMapX(-this.speed);
      this.speed *= -1;
      this.changeMapY(0);
    }

    if (this.isTouchingSprite(Fireball.class)) {
      this.remove();
    }

    if (state.equals("idle")) {
      this.setAnimationFrame(0);
    }

    this.playAnimation("walk-" + dir);
  }
}
