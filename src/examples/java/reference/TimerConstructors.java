package reference;
import org.openpatch.scratch.*;


public class TimerConstructors {
  public TimerConstructors() {
    Stage myStage = new Stage(600, 240);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // A timer of your own, counting from the moment it is built.
    Timer myTimer = new Timer();
    while (true) {
      if (myTimer.everyMillis(1000)) {
        mySprite.nextCostume();
      }
      myStage.wait(10);
    }
  }

  public static void main(String[] args) {
    new TimerConstructors();
  }
}
