package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** A spinning coffee bean to collect. */
public class Bean extends Sprite {

  public Bean(double x, double y) {
    this.addAnimation("spin", "coffee_bean_%d", 4);
    this.setAnimationInterval(0.12);
    this.setX(x);
    this.setY(y);
  }

  public void run() {
    this.playAnimation("spin");
  }
}
