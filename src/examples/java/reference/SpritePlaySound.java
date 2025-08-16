package reference;
import org.openpatch.scratch.*;

public class SpritePlaySound {

  public SpritePlaySound() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addSound("bump", "assets/bump.wav");
    myStage.add(zeta);

    while (myStage.getTimer().forMillis(3000)) {
      zeta.playSound("bump");
      myStage.wait(500);
    }
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePlaySound();
  }
}
