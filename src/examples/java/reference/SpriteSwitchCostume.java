package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSwitchCostume {
  public SpriteSwitchCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    zeta.addCostume("yellow", "slimeBlue");
    myStage.add(zeta);
    myStage.wait(3000);
    zeta.switchCostume("yellow");
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteSwitchCostume();
  }
}
