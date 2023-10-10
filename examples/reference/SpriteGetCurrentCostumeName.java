import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetCurrentCostumeName {
  public SpriteGetCurrentCostumeName() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.addCostume("gamma", "assets/gamma_purple_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    mySprite.think("Name: " + mySprite.getCurrentCostumeName());
    myStage.wait(2000);
    mySprite.nextCostume();
    mySprite.think("Name: " + mySprite.getCurrentCostumeName());
    myStage.wait(2000);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetCurrentCostumeName();
  }
}
