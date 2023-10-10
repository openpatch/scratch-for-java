import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetWidth {
  public SpriteGetWidth() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    mySprite.say("Width: " + mySprite.getWidth());
    recorder.start();
    myStage.wait(3000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetWidth();
  }
}
