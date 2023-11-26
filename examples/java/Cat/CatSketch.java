package Cat;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatSketch extends Stage {

  public CatSketch() {
    super(800, 600);
    Sprite myCat = new CatSprite();
    this.add(myCat);
  }

  public static void main(String[] args) {
    new CatSketch();
  }
}

class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "Cat/sprites/cat.png");
    this.setOnEdgeBounce(true);
    this.setDirection(0);
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == 32) {
      this.stamp();
    }
  }

  public void run() {
    this.move(2);
  }
}
