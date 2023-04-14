import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePointTowardsMousePointer {
  public SpritePointTowardsMousePointer() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);

    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.pointTowardsMousePointer();
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpritePointTowardsMousePointer();
  }
}
