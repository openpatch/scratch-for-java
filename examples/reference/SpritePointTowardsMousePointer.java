import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePointTowardsMousePointer {
  public SpritePointTowardsMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.pointTowardsMousePointer();
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePointTowardsMousePointer();
  }
}
