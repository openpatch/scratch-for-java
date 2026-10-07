package demos.catPlatformer;

import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.RotationStyle;

/** A bug walking to and fro. Jump on it or punch it - but do not walk into it. */
public class Bug extends AnimatedSprite {

  /** The costume is 32 pixels high, the legs end 3 pixels above its bottom. */
  private static final double FEET = 13;

  private final double minX;
  private final double maxX;
  private boolean squished = false;
  private int squishedFrames = 0;

  public Bug(double minX, double maxX, double floor) {
    this.addAnimation("walk", "bug_walk_%d", 4);
    this.addAnimation("squished", "bug_squished_%d", 2);
    this.setAnimationInterval(120);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.minX = minX;
    this.maxX = maxX;
    this.setX(minX);
    this.setY(floor + FEET);
  }

  public boolean isSquished() {
    return this.squished;
  }

  public void squish() {
    this.squished = true;
    this.resetAnimation();
  }

  public void run() {
    if (this.squished) {
      this.playAnimation("squished", true);
      this.squishedFrames += 1;
      if (this.squishedFrames > 30) {
        this.remove();
      }
      return;
    }
    this.playAnimation("walk");
    this.move(1);
    if (this.getX() > this.maxX) {
      this.setDirection(-90);
    } else if (this.getX() < this.minX) {
      this.setDirection(90);
    }
  }
}
