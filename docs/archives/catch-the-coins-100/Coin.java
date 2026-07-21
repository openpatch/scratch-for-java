import org.openpatch.scratch.*;

public class Coin extends Sprite {
  public Coin() {
    this.addCostume("coinGold");
    this.setSize(40);
    this.dropFromTop();
  }

  private void dropFromTop() {
    this.setX(Random.randomInt(-270, 270));
    this.setY(Random.randomInt(200, 400));
  }

  public void run() {
    this.changeY(-3);

    if (this.isTouchingSprite(Basket.class)) {
      ((CatchStage) this.getStage()).addPoint();
      this.dropFromTop();
    }

    if (this.getY() < -200) {
      this.dropFromTop();
    }
  }
}
