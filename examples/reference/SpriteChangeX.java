import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteChangeX {
  public SpriteChangeX() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    mySprite.changeX(100);
    myStage.wait(2000);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteChangeX();
  }
}
