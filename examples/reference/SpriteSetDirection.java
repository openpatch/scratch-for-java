import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetDirection {
  public SpriteSetDirection() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
    mySprite.setDirection(45);
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteSetDirection();
  }
}
