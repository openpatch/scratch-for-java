import org.openpatch.scratch.*;
import org.openpatch.scratch.Sprite;

public class SpriteStopAllSounds {

  public SpriteStopAllSounds() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addSound("bump", "assets/bump.wav");
    zeta.addSound("music", "assets/music.mp3");
    zeta.playSound("bump");
    zeta.playSound("music");
    myStage.wait(300);
    zeta.stopAllSounds();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteStopAllSounds();
  }
}
