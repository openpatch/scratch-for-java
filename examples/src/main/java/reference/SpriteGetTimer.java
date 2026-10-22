package reference;
import org.openpatch.scratch.*;


public class SpriteGetTimer {
  public SpriteGetTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // The sprite's own timer, and as many more as you need.
    Timer stepsTimer = new Timer();
    while (true) {
      if (mySprite.getTimer().every(1)) {
        mySprite.nextCostume();
      }
      if (stepsTimer.every(0.1)) {
        mySprite.changeX(5);
        mySprite.ifOnEdgeBounce();
      }
      myStage.waitSeconds(0.01);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTimer();
  }
}
