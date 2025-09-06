package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteRemoveTimer {
  public SpriteRemoveTimer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    // Add a custom timer
    mySprite.addTimer("customTimer");

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    mySprite.say("Timer added");
    myStage.wait(1500);
    
    // Remove the custom timer
    mySprite.removeTimer("customTimer");
    mySprite.say("Timer removed");
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteRemoveTimer();
  }
}