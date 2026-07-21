package reference.AnimatedSpriteSetAnimationInterval;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.AnimatedSprite;

public class MySprite extends AnimatedSprite {
  public MySprite() {
    this.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
  }

  public void run() {
    this.playAnimation("idle");
    if (this.isKeyPressed(KeyCode.S)) {
      this.setAnimationInterval(60);
    } else if (this.isKeyPressed(KeyCode.W)) {
      this.setAnimationInterval(10);
    }
  }
}
