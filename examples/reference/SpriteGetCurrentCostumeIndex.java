import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetCurrentCostumeIndex {
  public SpriteGetCurrentCostumeIndex() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.addCostume("gamma", "assets/gamma_purple_badge.png");
    mySprite.changeY(20);
    myStage.add(mySprite);
    mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    myStage.wait(2000);
    mySprite.nextCostume();
    mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());
    myStage.wait(2000);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetCurrentCostumeIndex();
  }
}
