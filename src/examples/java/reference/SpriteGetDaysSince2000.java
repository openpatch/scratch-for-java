package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetDaysSince2000 {
  public SpriteGetDaysSince2000() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Display days since 2000
    int daysSince2000 = mySprite.getDaysSince2000();
    mySprite.say("Days since 2000: " + daysSince2000);
    myStage.wait(3000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetDaysSince2000();
  }
}