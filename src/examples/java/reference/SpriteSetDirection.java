package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetDirection {
  public SpriteSetDirection() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
    mySprite.setDirection(45);
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetDirection();
  }
}
