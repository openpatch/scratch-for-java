package reference;
import org.openpatch.scratch.*;


public class SpriteGetTimer {
  public SpriteGetTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // The sprite's own timer, and as many more as you care to name.
    while (true) {
      if (mySprite.getTimer().everyMillis(1000)) {
        mySprite.nextCostume();
      }
      if (mySprite.getTimer("steps").everyMillis(100)) {
        mySprite.changeX(5);
        mySprite.ifOnEdgeBounce();
      }
      myStage.wait(10);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTimer();
  }
}
