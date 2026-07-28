package reference;
import org.openpatch.scratch.*;

public class SpriteGoLayersBackwards {
  public SpriteGoLayersBackwards() {
    Stage myStage = new Stage(600, 240);

    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(10);
    myStage.add(gamma);
    myStage.wait(1000);
    gamma.goLayersBackwards(2);
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteGoLayersBackwards();
  }
}
