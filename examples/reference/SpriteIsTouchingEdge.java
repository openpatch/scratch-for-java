import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsTouchingEdge {
  public SpriteIsTouchingEdge() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.changeY(-10);
    mySprite.changeX(-80);
    myStage.add(mySprite);

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      mySprite.say("Is touching edge? " + mySprite.isTouchingEdge());
      mySprite.changeY(10);
      myStage.wait(200);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsTouchingEdge();
  }
}
