package reference;

import org.openpatch.scratch.Origin;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetOrigin {
  public SpriteSetOrigin() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);
    mySprite.setPosition(0, 0);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    // Show default center origin
    mySprite.say("Origin: CENTER (default)");
    myStage.wait(1000);

    // Demonstrate TOP_LEFT origin
    mySprite.setOrigin(Origin.TOP_LEFT);
    mySprite.say("Origin: TOP_LEFT");
    myStage.wait(1000);

    // Demonstrate TOP_RIGHT origin
    mySprite.setOrigin(Origin.TOP_RIGHT);
    mySprite.say("Origin: TOP_RIGHT");
    myStage.wait(1000);

    // Demonstrate BOTTOM_LEFT origin
    mySprite.setOrigin(Origin.BOTTOM_LEFT);
    mySprite.say("Origin: BOTTOM_LEFT");
    myStage.wait(1000);

    // Demonstrate custom origin offset
    mySprite.setOrigin(-30, 20);
    mySprite.say("Origin: Custom (-30, 20)");
    myStage.wait(1000);

    // Back to center
    mySprite.setOrigin(Origin.CENTER);
    mySprite.say("Origin: CENTER");
    myStage.wait(1000);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetOrigin();
  }
}
