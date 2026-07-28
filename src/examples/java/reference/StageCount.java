package reference;
import org.openpatch.scratch.*;


public class StageCount {
  public StageCount() {
    class Coin extends Sprite {
      public Coin() {
        this.addCostume("coin", "coinGold");
        this.goToRandomPosition();
      }
    }

    Stage myStage = new Stage(600, 240);
    // How many sprites of one kind are on the stage - the usual way of asking
    // "have they all been collected?".
    for (int i = 0; i < 10; i++) {
      myStage.add(new Coin());
      System.out.println(myStage.count(Coin.class) + " coins");
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StageCount();
  }
}
