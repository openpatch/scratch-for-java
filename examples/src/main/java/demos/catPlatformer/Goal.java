package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** The steaming mug at the end of the level. */
public class Goal extends Sprite {

  public Goal(double x, double floor) {
    this.addAnimation("steam", "goal_mug_%d", 4);
    this.setAnimationInterval(0.15);
    this.setX(x);
    // the mug stands on the bottom two rows of its 32 pixel costume
    this.setY(floor + 14);
  }

  public void run() {
    this.playAnimation("steam");
  }
}
