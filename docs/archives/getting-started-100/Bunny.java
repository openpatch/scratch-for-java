import org.openpatch.scratch.*;

public class Bunny extends Sprite {
  public Bunny() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("bunny1_stand");
    // scratch4j:end setup
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
