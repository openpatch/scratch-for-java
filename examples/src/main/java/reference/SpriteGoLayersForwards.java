package reference;
import org.openpatch.scratch.*;

public class SpriteGoLayersForwards {
  public SpriteGoLayersForwards() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(10);
    myStage.add(gamma);
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.waitSeconds(1);
    gamma.goLayersForwards(2);
    myStage.waitSeconds(1);
    gamma.goLayersForwards(3);
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteGoLayersForwards();
  }
}
