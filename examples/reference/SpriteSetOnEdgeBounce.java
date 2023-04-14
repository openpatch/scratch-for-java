import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetOnEdgeBounce {
  public SpriteSetOnEdgeBounce() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);
    mySprite.setOnEdgeBounce(true);
    mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);

    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.move(1);
      myStage.wait(20);
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteSetOnEdgeBounce();
  }
}
