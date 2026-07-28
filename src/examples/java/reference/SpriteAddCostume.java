package reference;
import org.openpatch.scratch.*;

public class SpriteAddCostume {
  public SpriteAddCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    zeta.addCostume("yellow", "slimeBlue");
    myStage.add(zeta);
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteAddCostume();
  }
}
