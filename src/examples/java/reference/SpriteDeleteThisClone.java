package reference;
import org.openpatch.scratch.*;

public class SpriteDeleteThisClone {
  public SpriteDeleteThisClone() {
    class Coin extends Sprite {
      public Coin() {
        this.addCostume("coin", "coinGold");
        this.setY(100);
      }

      public void run() {
        if (!this.isClone()) {
          // The original drops a new coin every second and stays where it is.
          if (this.getTimer().everyMillis(1000)) {
            this.clone();
          }
          return;
        }
        this.changeY(-4);
        // A coin that leaves the stage is gone; the original is never deleted.
        if (this.getY() < -120) {
          this.deleteThisClone();
        }
      }
    }

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.add(new Coin());
  }

  public static void main(String[] args) {
    new SpriteDeleteThisClone();
  }
}
