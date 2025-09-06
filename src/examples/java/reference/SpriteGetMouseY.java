package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetMouseY {
  public SpriteGetMouseY() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Show mouse Y position for a few seconds
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.say("Mouse Y: " + (int) mySprite.getMouseY());
      myStage.wait(100);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetMouseY();
  }
}