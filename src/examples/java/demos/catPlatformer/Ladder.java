package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** One piece of the ladder. Touching it, the cat can climb with the up and down keys. */
public class Ladder extends Sprite {

  public Ladder(double x, double y, boolean top) {
    this.addCostume(top ? "ladderTop" : "ladderMid");
    this.setSize(50);
    this.setX(x);
    this.setY(y);
  }
}
