package reference;
import org.openpatch.scratch.*;

public class SpriteAddCostume {
  public SpriteAddCostume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    zeta.addCostume("yellow", "slimeBlue");
    myStage.add(zeta);
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteAddCostume();
  }
}
