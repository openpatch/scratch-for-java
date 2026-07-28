package reference;
import org.openpatch.scratch.*;

public class SpriteGoToFrontLayer {
  public SpriteGoToFrontLayer() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.wait(1000);
    gamma.goToFrontLayer();
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteGoToFrontLayer();
  }
}
