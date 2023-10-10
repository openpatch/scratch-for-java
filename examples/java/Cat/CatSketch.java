package Cat;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatSketch {

  public CatSketch() {
    Stage myStage = new Stage(800, 600);
    Sprite myCat = new Sprite();
    myStage.add(myCat);
  }

  public void run() {}

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

  public void run() {
    this.move(2);
  }
}
