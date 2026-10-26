import org.openpatch.scratch.*;

public class Alien extends Sprite {
  public Alien() {
    // begin sprite editor
    this.addCostume("alienGreen_stand");
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(0.15);
    // end sprite editor
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
