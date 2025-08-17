package demos.donutIO;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.shape.Ellipse;

public class Donut extends Sprite {

  private int strength;
  protected double speed = 1;

  public Donut() {
    this(0, 0, 2);
  }

  public Donut(double x, double y, int strength) {
    this.addCostume("donut", "demos/donutIO/assets/donut.png");
    this.setHitbox(new Ellipse(0, 0, 512, 480));
    this.setX(x);
    this.setY(y);

    this.setStrength(strength);

    this.setTint(Random.randomInt(240), Random.randomInt(240), Random.randomInt(240));
  }

  public void setStrength(int strength) {
    this.strength = strength;
    this.setSize(strength);
  }

  public double getStrength() {
    return this.strength;
  }

  public void run() {
    var touchingDonut = this.getTouchingSprite(Donut.class);
    if (touchingDonut != null && this.strength >= touchingDonut.strength) {
      this.setStrength(this.strength + touchingDonut.strength);
      touchingDonut.remove();
    }
  }
}
