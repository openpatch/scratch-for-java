package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsUI {
  public SpriteIsUI() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Check if sprite is UI initially
    mySprite.say("Is UI: " + mySprite.isUI());
    myStage.wait(1500);
    
    // Set as UI sprite
    mySprite.isUI(true);
    mySprite.say("Is UI: " + mySprite.isUI());
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsUI();
  }
}