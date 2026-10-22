package reference;
import org.openpatch.scratch.*;


public class StageGetTimer {
  public StageGetTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // The stage's own timer, and as many more as you need.
    Timer colourTimer = new Timer();
    while (true) {
      if (myStage.getTimer().every(1)) {
        mySprite.nextCostume();
      }
      if (colourTimer.every(2)) {
        Color color = myStage.getColor();
        myStage.setColor(color.withHue(color.getHue() + 40));
      }
      myStage.waitSeconds(0.01);
    }
  }

  public static void main(String[] args) {
    new StageGetTimer();
  }
}
