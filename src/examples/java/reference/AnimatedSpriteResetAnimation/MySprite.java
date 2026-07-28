package reference.AnimatedSpriteResetAnimation;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.AnimatedSprite;

public class MySprite extends AnimatedSprite {
  public MySprite() {
    this.addAnimation("idle", "bunny1_walk%d", 2);
  }

  public void run() {
    this.playAnimation("idle");

    if (this.isKeyPressed(KeyCode.SPACE)) {
      this.resetAnimation();
    }
  }
}
