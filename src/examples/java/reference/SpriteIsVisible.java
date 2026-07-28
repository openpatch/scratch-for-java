package reference;
import org.openpatch.scratch.*;

public class SpriteIsVisible {
  public SpriteIsVisible() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.wait(2000);
    gamma.hide();
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteIsVisible();
  }
}
