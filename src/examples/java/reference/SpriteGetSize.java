package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetSize {
  public SpriteGetSize() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    
    // Set a custom size
    mySprite.setSize(150);
    
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    mySprite.say("Size: " + mySprite.getSize() + "%");
    recorder.start();
    myStage.wait(3000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetSize();
  }
}