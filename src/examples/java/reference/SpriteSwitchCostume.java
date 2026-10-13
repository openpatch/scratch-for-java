package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteSwitchCostume {
  public SpriteSwitchCostume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
