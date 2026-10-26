package reference;
import org.openpatch.scratch.*;

public class SpriteIsVisible {
  public SpriteIsVisible() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.waitSeconds(2);
    gamma.hide();
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new SpriteIsVisible();
  }
}
