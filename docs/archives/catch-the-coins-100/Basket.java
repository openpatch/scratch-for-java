import org.openpatch.scratch.*;

public class Basket extends Sprite {
  public Basket() {
    // begin sprite editor
    this.addCostume("alienGreen_stand");
    // end sprite editor
    this.setSize(35);
    this.setY(-150);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(5);
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(5);
    }
    this.ifOnEdgeBounce();
  }
}
