package reference.SpriteShow;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("slime", "assets/slime.png");
  }

  public void run() {
    // The hide/show logic is handled in the window timing
  }
}