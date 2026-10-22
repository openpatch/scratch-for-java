package demos.tiled;

import org.openpatch.scratch.*;

public class Fireball extends Sprite {
  private String dir;
  private double speed = 3;

  public Fireball(double x, double y, String dir) {
    this.setX(x);
    this.setY(y);
    this.dir = dir;

    // begin sprite editor
    this.addAnimation("default", "demos/tiled/assets/Fireball.png", 32, 32, 0, 1, 2, 3);
    // end sprite editor
  }

  public void run() {
    if (this.getTimer().after(1)) {
      this.remove();
    }

    if (dir == "left") {
      this.changeX(-speed);
      this.setDirection(0);
    } else if (dir == "right") {
      this.changeX(speed);
      this.setDirection(180);
    } else if (dir == "up") {
      this.setDirection(90);
      this.changeY(speed);
    } else if (dir == "down") {
      this.setDirection(270);
      this.changeY(-speed);
    }

    this.playAnimation("default");
  }
}
