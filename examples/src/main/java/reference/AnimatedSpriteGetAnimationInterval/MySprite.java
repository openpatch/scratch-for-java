package reference.AnimatedSpriteGetAnimationInterval;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addAnimation("idle", "bunny1_walk%d", 2);
  }

  public void run() {
    this.playAnimation("idle");
    this.say("Interval:" + this.getAnimationInterval());
  }
}
