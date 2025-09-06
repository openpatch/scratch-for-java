package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetNineSlice {
  public SpriteSetNineSlice() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Set nine-slice parameters (top, right, bottom, left)
    mySprite.setNineSlice(10, 10, 10, 10);
    mySprite.say("Nine-slice enabled");
    myStage.wait(1500);
    
    // Change size to show nine-slice effect
    mySprite.setSize(200);
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetNineSlice();
  }
}