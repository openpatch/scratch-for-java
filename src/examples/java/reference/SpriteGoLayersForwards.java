package reference;
import org.openpatch.scratch.*;

public class SpriteGoLayersForwards {
  public SpriteGoLayersForwards() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(10);
    myStage.add(gamma);
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.wait(1000);
    gamma.goLayersForwards(2);
    myStage.wait(1000);
    gamma.goLayersForwards(3);
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteGoLayersForwards();
  }
}
