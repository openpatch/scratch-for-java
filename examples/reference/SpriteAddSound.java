import org.openpatch.scratch.*;

public class SpriteAddSound {

  public SpriteAddSound() {
    var myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addSound("bump", "assets/bump.wav");
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteAddSound();
  }
}
