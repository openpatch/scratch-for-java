package reference;
import org.openpatch.scratch.*;


public class StageGetTimer {
  public StageGetTimer() {
    Stage myStage = new Stage(600, 240);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // The stage's own timer, and as many more as you care to name.
    while (true) {
      if (myStage.getTimer().everyMillis(1000)) {
        mySprite.nextCostume();
      }
      if (myStage.getTimer("colour").everyMillis(2000)) {
        myStage.changeColor(40);
      }
      myStage.wait(10);
    }
  }

  public static void main(String[] args) {
    new StageGetTimer();
  }
}
