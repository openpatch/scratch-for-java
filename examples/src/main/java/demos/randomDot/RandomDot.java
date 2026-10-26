package demos.randomDot;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class RandomDot extends Stage {

  // begin stage designer
  private RandomDotSprite randomDotSprite;
  // end stage designer

  public RandomDot() {
    // begin stage designer
    randomDotSprite = new RandomDotSprite();
    this.add(randomDotSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 600);
    myWindow.setStage(new RandomDot());
  }
}

class RandomDotSprite extends Sprite {
  public void run() {
    if (this.getTimer().every(0.1)) {
      this.getPen().up();
      this.getPen().setSize(20);
      this.goToRandomPosition();
      this.getPen().changeColor(2);
      this.getPen().down();
    }
  }
}
