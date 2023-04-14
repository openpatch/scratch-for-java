import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetX {
  public SpriteGetX() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    mySprite.say("X: " + mySprite.getX());
    myStage.wait(3000);
    recorder.snapshot();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteGetX();
  }
}
