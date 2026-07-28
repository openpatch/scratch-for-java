package reference;
import org.openpatch.scratch.*;


public class SpriteGetTouchingSprites {
  public SpriteGetTouchingSprites() {
    class Coin extends Sprite {
      public Coin() {
        this.addCostume("coin", "coinGold");
        this.goToRandomPosition();
      }
    }

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      myStage.add(new Coin());
    }

    Sprite player = new Sprite("player", "slimeGreen");
    player.setSize(200);
    myStage.add(player);

    // Every coin the player is on at once, which a big player may well be
    // several of.
    while (true) {
      player.goToMousePointer();
      player.say("touching " + player.getTouchingSprites(Coin.class).size() + " coins");
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTouchingSprites();
  }
}
