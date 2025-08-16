package reference;
import org.openpatch.scratch.*;

public class SpriteRemoveSound {

  public SpriteRemoveSound() {
    var myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addSound("bump", "assets/bump.wav");
    zeta.removeSound("bump");
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteRemoveSound();
  }
}
