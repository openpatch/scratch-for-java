package demos.randomDot;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class RandomDot extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private RandomDotSprite randomDotSprite;
  // scratch4j:end fields

  public RandomDot() {
    super(800, 600);
    // scratch4j:begin setup (managed by the stage designer)
    randomDotSprite = new RandomDotSprite();
    this.add(randomDotSprite);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new RandomDot();
  }
}

class RandomDotSprite extends Sprite {
  public void run() {
    if (this.getTimer().everyMillis(100)) {
      this.getPen().up();
      this.getPen().setSize(20);
      this.goToRandomPosition();
      this.getPen().changeColor(2);
      this.getPen().down();
    }
  }
}
