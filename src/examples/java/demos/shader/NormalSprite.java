package demos.shader;

import org.openpatch.scratch.Sprite;

public class NormalSprite extends Sprite {
  public NormalSprite() {
    // begin sprite editor
    this.addCostume("cat", "demos/shader/cat.png");
    // end sprite editor
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.move(5);
  }
}
