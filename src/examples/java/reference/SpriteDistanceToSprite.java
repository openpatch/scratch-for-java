package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteDistanceToSprite {
  public SpriteDistanceToSprite() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.setPosition(-120, 50);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "slimeGreen");
    zeta.setPosition(100, 50);
    myStage.add(zeta);
    while (true) {
      gamma.changeX(5);
      zeta.changeX(-5);
      myStage.display("Distance: " + gamma.distanceToSprite(zeta));
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteDistanceToSprite();
  }
}
