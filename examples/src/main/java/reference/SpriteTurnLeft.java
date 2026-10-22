package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteTurnLeft {
  public SpriteTurnLeft() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    while (true) {
      zeta.turnLeft(2);
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new SpriteTurnLeft();
  }
}
