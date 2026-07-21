package demos.coinCollector;

import org.openpatch.scratch.Sprite;

/** One grass tile of the ground. */
public class Ground extends Sprite {

  public Ground(double x) {
    this.addCostume("grassMid");
    this.setSize(50);
    this.setX(x);
    // The tile is 128 pixels high, so its middle sits 32 below its top edge.
    this.setY(CoinCollector.GROUND_TOP - 32);
  }
}
