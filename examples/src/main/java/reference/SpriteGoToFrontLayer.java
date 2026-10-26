package reference;
import org.openpatch.scratch.*;

public class SpriteGoToFrontLayer {
  public SpriteGoToFrontLayer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.waitSeconds(1);
    gamma.goToFrontLayer();
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteGoToFrontLayer();
  }
}
