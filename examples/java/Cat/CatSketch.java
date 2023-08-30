import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatSketch {

  public CatSketch() {
    Stage myStage = new Stage(800, 600);
    myStage.setDebug(true);
    Sprite myCat = new CatSprite();
    myStage.add(myCat);
  }

  public static void main(String[] args) {
    new CatSketch();
  }
}

class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "sprites/cat.png");
    this.setOnEdgeBounce(true);
    this.setDirection(0);
  }

  public void run() {
    this.move(2);
  }
}
