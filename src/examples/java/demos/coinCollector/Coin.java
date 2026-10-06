package demos.coinCollector;

import org.openpatch.scratch.Sprite;

/** A coin the player can pick up. It bobs up and down a little. */
public class Coin extends Sprite {

  private final double startY;
  private double angle = 0;

  public Coin(double x, double y) {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("coinGold");
    // scratch4j:end setup
    this.setSize(50);
    this.setX(x);
    this.setY(y);
    this.startY = y;
  }

  public void run() {
    this.angle += 3;
    this.setY(this.startY + Math.sin(Math.toRadians(this.angle)) * 8);
  }
}
