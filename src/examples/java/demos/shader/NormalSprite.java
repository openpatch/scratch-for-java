package demos.shader;

import org.openpatch.scratch.Sprite;

public class NormalSprite extends Sprite {
  public NormalSprite() {
    this.addCostume("cat", "demos/shader/cat.png");
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.move(5);
  }
}
