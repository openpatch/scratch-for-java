package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteIsTouchingSprite {
  public SpriteIsTouchingSprite() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.setPosition(-120, 50);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "slimeGreen");
    zeta.setPosition(120, 50);
    myStage.add(zeta);
    while (!gamma.isTouchingSprite(zeta)) {
      gamma.changeX(5);
      zeta.changeX(-5);
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteIsTouchingSprite();
  }
}
