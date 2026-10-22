package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** One grass tile of the ground. */
public class Ground extends Sprite {

  public Ground(double x) {
    this.addCostume("grassMid");
    this.setSize(50);
    this.setX(x);
    // The tile is 64 pixels high at size 50, so its middle sits 32 below its top.
    this.setY(CatPlatformer.GROUND_TOP - 32);
  }
}
