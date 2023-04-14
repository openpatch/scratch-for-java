import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetHeight {
  public SpriteGetHeight() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.changeY(30);
    myStage.add(mySprite);
    mySprite.say("Height: " + mySprite.getHeight());
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    myStage.wait(1000);
    recorder.snapshot();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteGetHeight();
  }
}
