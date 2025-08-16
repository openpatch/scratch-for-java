package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetTint {
  public SpriteSetTint() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    mySprite.setTint(200);
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetTint();
  }
}
