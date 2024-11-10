import org.openpatch.scratch.Sprite;

public class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "cat.png");
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
