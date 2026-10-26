package reference.AnimatedSpriteSetAnimationInterval;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addAnimation("idle", "bunny1_walk%d", 2);
  }

  public void run() {
    this.playAnimation("idle");
    if (this.isKeyPressed(KeyCode.S)) {
      this.setAnimationInterval(0.06);
    } else if (this.isKeyPressed(KeyCode.W)) {
      this.setAnimationInterval(0.01);
    }
  }
}
