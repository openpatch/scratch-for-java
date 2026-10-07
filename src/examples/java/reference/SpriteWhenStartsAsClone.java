package reference;
import org.openpatch.scratch.*;

public class SpriteWhenStartsAsClone {
  public SpriteWhenStartsAsClone() {
    class Star extends Sprite {
      public Star() {
        this.addCostume("star", "star1");
        this.hide();
      }

      // Every clone appears somewhere else and turns on its own.
      public void whenStartsAsClone() {
        this.goToRandomPosition();
        this.show();
      }

      public void run() {
        if (this.isClone()) {
          this.turnRight(3);
        } else if (this.getTimer().everyMillis(500)) {
          this.clone();
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new Star());
  }

  public static void main(String[] args) {
    new SpriteWhenStartsAsClone();
  }
}
