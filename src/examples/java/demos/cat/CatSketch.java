package demos.cat;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class CatSketch extends Stage {

  // begin stage designer
  private CatSprite myCat;
  // end stage designer

  public CatSketch() {
    // begin stage designer
    myCat = new CatSprite();
    this.add(myCat);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 600);
    myWindow.setStage(new CatSketch());
  }
}

class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "demos/cat/sprites/cat.png");
    this.setDirection(0);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      this.stamp();
    }
  }

  public void run() {
    this.move(2);
    this.ifOnEdgeBounce();
  }
}
