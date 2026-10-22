package reference;
import org.openpatch.scratch.*;

public class SpriteGoToBackLayer {
  public SpriteGoToBackLayer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(gamma);
    myStage.waitSeconds(1);
    gamma.goToBackLayer();
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteGoToBackLayer();
  }
}
