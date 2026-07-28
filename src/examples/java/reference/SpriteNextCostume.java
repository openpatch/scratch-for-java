package reference;
import org.openpatch.scratch.*;

public class SpriteNextCostume {
  public SpriteNextCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    zeta.addCostume("yellow", "slimeBlue");
    myStage.add(zeta);
    myStage.wait(2000);
    zeta.nextCostume();
    myStage.wait(2000);
    zeta.nextCostume();
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteNextCostume();
  }
}
