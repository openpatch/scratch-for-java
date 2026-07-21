import org.openpatch.scratch.*;

public class Alien extends AnimatedSprite {
  public Alien() {
    this.addCostume("alienGreen_stand");
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(150);
    this.setSize(45);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setY(-140);
  }

  public void run() {
    boolean walking = false;

    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(4);
      walking = true;
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(4);
      walking = true;
    }

    if (walking) {
      this.playAnimation("walk");
    } else {
      this.switchCostume("alienGreen_stand");
    }

    this.ifOnEdgeBounce();
  }
}
