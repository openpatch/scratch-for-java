package reference;
import org.openpatch.scratch.*;


public class TimerConstructors {
  public TimerConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // A timer of your own, counting from the moment it is built.
    Timer myTimer = new Timer();
    while (true) {
      if (myTimer.every(1)) {
        mySprite.nextCostume();
      }
      myStage.waitSeconds(0.01);
    }
  }

  public static void main(String[] args) {
    new TimerConstructors();
  }
}
