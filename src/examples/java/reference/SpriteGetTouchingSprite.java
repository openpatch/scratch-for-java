package reference;
import org.openpatch.scratch.*;


public class SpriteGetTouchingSprite {
  public SpriteGetTouchingSprite() {
    class Coin extends Sprite {
      public Coin() {
        this.addCostume("coin", "coinGold");
        this.goToRandomPosition();
      }
    }

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 8; i++) {
      myStage.add(new Coin());
    }

    Sprite player = new Sprite("player", "slimeGreen");
    myStage.add(player);

    // The one coin the player is on, or null when it is on none.
    while (true) {
      player.goToMousePointer();
      Coin touched = player.getTouchingSprite(Coin.class);
      if (touched != null) {
        touched.remove();
      }
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTouchingSprite();
  }
}
