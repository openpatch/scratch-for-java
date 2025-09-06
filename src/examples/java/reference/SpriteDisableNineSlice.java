package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteDisableNineSlice {
  public SpriteDisableNineSlice() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    // First enable nine-slice
    mySprite.setNineSlice(10, 10, 10, 10);
    mySprite.setSize(200);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    mySprite.say("Nine-slice enabled");
    myStage.wait(1500);
    
    // Disable nine-slice
    mySprite.disableNineSlice();
    mySprite.say("Nine-slice disabled");
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteDisableNineSlice();
  }
}