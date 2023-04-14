import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetRotationStyle {
  public SpriteSetRotationStyle() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    mySprite.say("Rotation: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setDirection(45);
    mySprite.say("All-Around: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setRotationStyle(RotationStyle.DONT);
    mySprite.setDirection(180);
    mySprite.say("Don't: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
    mySprite.setDirection(200);
    mySprite.say("LEFT-RIGHT: " + mySprite.getDirection());
    myStage.wait(1000);
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteSetRotationStyle();
  }
}
