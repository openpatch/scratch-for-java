package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsTouchingSprite {
  public SpriteIsTouchingSprite() {
    Stage myStage = new Stage(600, 240);

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
