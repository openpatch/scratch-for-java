package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetMouse {
  public SpriteGetMouse() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.changeY(30);
    mySprite.changeX(-100);
    myStage.add(mySprite);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.say("X: " + mySprite.getMouseX() + " Y: " + mySprite.getMouseY());
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetMouse();
  }
}
