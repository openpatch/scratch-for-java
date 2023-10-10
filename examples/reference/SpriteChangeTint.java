import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteChangeTint {
  public SpriteChangeTint() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.changeTint(150);
    myStage.wait(2000);

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteChangeTint();
  }
}
