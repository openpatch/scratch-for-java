package reference.AnimatedSpriteIsAnimationPlayed;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addAnimation("idle", "bunny1_walk%d", 2);
  }

  public void run() {
    this.playAnimation("idle", true);
    if (this.isAnimationPlayed()) {
      this.say("Animation is played");
    } else {
      this.say("Animation is not played");
    }
  }
}
