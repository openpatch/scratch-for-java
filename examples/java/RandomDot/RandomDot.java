import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.math.Random;

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
      this.getPen().down();
      this.getPen().setSize(10);
      this.setPosition(
          Random.randomInt(-400, 400),
          Random.randomInt(-300, 300));
      this.getPen().changeColor(2);
      this.getPen().up();
    }
  }
}
