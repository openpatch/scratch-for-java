package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteTurnLeft {
  public SpriteTurnLeft() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    while (true) {
      zeta.turnLeft(2);
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new SpriteTurnLeft();
  }
}
