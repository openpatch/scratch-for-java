package demos.tiled;

public class Bamboo extends Enemy {

  protected double speed = 2.5;

  public Bamboo(double x, double y) {
    super(x, y);

    this.addAnimation("walk-down", "demos/tiled/assets/Bamboo.png", 4, 32, 32, 0, true);
    this.addAnimation("walk-up", "demos/tiled/assets/Bamboo.png", 4, 32, 32, 1, true);
    this.addAnimation("walk-left", "demos/tiled/assets/Bamboo.png", 4, 32, 32, 2, true);
    this.addAnimation("walk-right", "demos/tiled/assets/Bamboo.png", 4, 32, 32, 3, true);
  }

  public void run() {
    state = "walk";

    this.changeX(this.speed);

    if (this.speed > 0) {
      dir = "right";
    } else {
      dir = "left";
    }
    this.changeY(0);
    if (this.isTouchingSprite(Wall.class)) {
      state = "idle";
      this.changeX(-this.speed);
      this.speed *= -1;
      this.changeY(0);
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
