package reference.AnimatedSpriteSetAnimationFrame;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.AnimatedSprite;

public class MySprite extends AnimatedSprite {
  public MySprite() {
    this.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
  }

  public void run() {
    this.playAnimation("idle");
    if (this.isKeyPressed(KeyCode.SPACE)) {
      this.setAnimationFrame(0);
    }
  }
}
