package reference;

import org.openpatch.scratch.Origin;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetOrigin {
  public SpriteGetOrigin() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);
    mySprite.setPosition(-100, 0);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    // Show origin getter methods
    mySprite.say("getOrigin(): " + mySprite.getOrigin());
    myStage.wait(1500);

    // Set custom origin and show getters
    mySprite.setOrigin(-25, 15);
    mySprite.say(
        "getOriginX(): " + mySprite.getOriginX() + ", getOriginY(): " + mySprite.getOriginY());
    myStage.wait(1500);

    // Set preset origin and show computed values
    mySprite.setOrigin(Origin.TOP_LEFT);
    mySprite.say(
        "TOP_LEFT -> X:" + (int) mySprite.getOriginX() + ", Y:" + (int) mySprite.getOriginY());
    myStage.wait(1500);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetOrigin();
  }
}
