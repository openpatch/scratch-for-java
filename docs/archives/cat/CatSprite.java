import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "cat.png");
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
