package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsTouchingMousePointer {
  public SpriteIsTouchingMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.changeX(-100);
    mySprite.changeY(30);
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.say("Is touching mouse? " + mySprite.isTouchingMousePointer());
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsTouchingMousePointer();
  }
}
