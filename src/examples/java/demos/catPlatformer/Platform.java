package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** One tile of a platform the cat can stand on. */
public class Platform extends Sprite {

  private final double top;

  public Platform(String costume, double x, double top) {
    this.addCostume(costume);
    this.setSize(50);
    this.top = top;
    this.setX(x);
    // The grass of a half tile starts at the top of its 64 pixel costume.
    this.setY(top - 32);
  }

  public double getTop() {
    return this.top;
  }

  public double getLeft() {
    return this.getX() - 32;
  }

  public double getRight() {
    return this.getX() + 32;
  }
}
