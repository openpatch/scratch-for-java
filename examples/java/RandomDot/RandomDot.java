package RandomDot;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class RandomDot extends Stage {
  public RandomDot() {
    super(800, 600);
    this.add(new RandomDotSprite());
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
