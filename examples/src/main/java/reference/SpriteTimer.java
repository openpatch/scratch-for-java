package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Timer;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteTimer {
  public SpriteTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite timeMe = new Sprite("zeta", "slimeGreen");
    timeMe.addCostume("gamma", "slimePurple");
    myStage.add(timeMe);
    Timer identityChange = new Timer();
    while (true) {
      if (identityChange.every(1)) {
        timeMe.nextCostume();
      }
    }
  }

  public static void main(String[] args) {
    new SpriteTimer();
  }
}
