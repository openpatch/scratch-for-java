package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteTimer {
  public SpriteTimer() {
    Stage myStage = new Stage(600, 240);
    Sprite timeMe = new Sprite("zeta", "slimeGreen");
    timeMe.addCostume("gamma", "slimePurple");
    myStage.add(timeMe);
    while (true) {
      if (timeMe.getTimer("identityChange").everyMillis(1000)) {
        timeMe.nextCostume();
      }
    }
  }

  public static void main(String[] args) {
    new SpriteTimer();
  }
}
