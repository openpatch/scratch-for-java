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
