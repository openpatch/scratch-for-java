import org.openpatch.scratch.*;

public class Rock extends Sprite {
  public Rock() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("rock");
    // scratch4j:end setup
    this.setSize(40);
    this.dropFromTop();
  }

  private void dropFromTop() {
    this.setX(Random.randomInt(-280, 280));
    this.setY(Random.randomInt(220, 420));
  }

  public void run() {
    this.changeY(-3);

    if (this.isTouchingSprite(Alien.class)) {
      ((GameStage) this.getStage()).gameOver();
    }

    if (this.getY() < -200) {
      ((GameStage) this.getStage()).addDodge();
      this.dropFromTop();
    }
  }
}
