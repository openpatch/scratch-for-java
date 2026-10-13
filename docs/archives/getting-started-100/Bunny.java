import org.openpatch.scratch.*;

public class Bunny extends Sprite {
  public Bunny() {
    // begin sprite editor
    this.addCostume("bunny1_stand");
    // end sprite editor
    this.setSize(50);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(4);
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(4);
    }
    this.ifOnEdgeBounce();
  }
}
