package demos.catPlatformer;

import org.openpatch.scratch.AnimatedSprite;

/** A spinning coffee bean to collect. */
public class Bean extends AnimatedSprite {

  public Bean(double x, double y) {
    this.addAnimation("spin", "coffee_bean_%d", 4);
    this.setAnimationInterval(120);
    this.setX(x);
    this.setY(y);
  }

  public void run() {
    this.playAnimation("spin");
  }
}
