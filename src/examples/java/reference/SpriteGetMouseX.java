package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetMouseX {
  public SpriteGetMouseX() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Show mouse X position for a few seconds
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.say("Mouse X: " + (int) mySprite.getMouseX());
      myStage.wait(100);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetMouseX();
  }
}