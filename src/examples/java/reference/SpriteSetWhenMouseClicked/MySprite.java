package reference.SpriteSetWhenMouseClicked;

import org.openpatch.scratch.MouseCode;
import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    
    // Set a custom mouse click handler
    this.setWhenMouseClicked((sprite, mouseCode) -> {
      if (mouseCode == MouseCode.LEFT) {
        sprite.say("Left click detected!");
      } else if (mouseCode == MouseCode.RIGHT) {
        sprite.say("Right click detected!");
      } else {
        sprite.say("Mouse clicked: " + mouseCode);
      }
    });
  }
}