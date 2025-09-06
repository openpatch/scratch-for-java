package reference.SpriteSetRun;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  private int counter = 0;

  public MySprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    
    // Set a custom run handler
    this.setRun((sprite) -> {
      counter++;
      this.say("Frame: " + counter);
    });
  }
}