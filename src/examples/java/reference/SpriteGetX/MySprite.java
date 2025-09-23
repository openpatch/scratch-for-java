package reference.SpriteGetX;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    this.changeX(-80);
    this.changeY(30);
  }

  public void run() {
    this.say("X: " + this.getX());
  }
}