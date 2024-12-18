package Shader;

import org.openpatch.scratch.Sprite;

public class NormalSprite extends Sprite {
  public NormalSprite() {
    this.addCostume("cat", "Shader/cat.png");
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.move(5);
  }
}
