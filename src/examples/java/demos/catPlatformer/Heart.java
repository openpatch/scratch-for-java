package demos.catPlatformer;

import org.openpatch.scratch.Sprite;

/** One of the cat's lives, shown in the top left corner. */
public class Heart extends Sprite {

  public Heart(double x, double y) {
    this.addCostume("heart_full");
    this.addCostume("heart_empty");
    this.setX(x);
    this.setY(y);
  }

  public boolean isFull() {
    return this.getCurrentCostumeName().equals("heart_full");
  }

  public void empty() {
    this.switchCostume("heart_empty");
  }
}
