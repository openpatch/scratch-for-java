package reference;
import org.openpatch.scratch.*;

public class SpriteGoLayersBackwards {
  public SpriteGoLayersBackwards() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    myStage.add(new Sprite("zeta", "slimeGreen"));
    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(10);
    myStage.add(gamma);
    myStage.waitSeconds(1);
    gamma.goLayersBackwards(2);
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteGoLayersBackwards();
  }
}
