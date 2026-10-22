import org.openpatch.scratch.*;

public class Cat extends Sprite {
  public Cat() {
    this.addCostume("sit", "assets/cat.png");
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.move(1);
  }
}
