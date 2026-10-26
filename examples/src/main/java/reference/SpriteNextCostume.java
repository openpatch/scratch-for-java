package reference;
import org.openpatch.scratch.*;

public class SpriteNextCostume {
  public SpriteNextCostume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    zeta.addCostume("yellow", "slimeBlue");
    myStage.add(zeta);
    myStage.waitSeconds(2);
    zeta.nextCostume();
    myStage.waitSeconds(2);
    zeta.nextCostume();
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new SpriteNextCostume();
  }
}
