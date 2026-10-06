package demos.cat;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatSketch extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private CatSprite myCat;
  // scratch4j:end fields

  public CatSketch() {
    super(800, 600);
    // scratch4j:begin setup (managed by the stage designer)
    myCat = new CatSprite();
    this.add(myCat);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new CatSketch();
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
