import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetSize {
  public SpriteSetSize() {
    Stage myStage = new Stage(254, 100);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    mySprite.say("Size: " + mySprite.getSize());
    myStage.wait(2000);
    mySprite.setSize(50);
    mySprite.say("Size: " + mySprite.getSize());
    myStage.wait(2000);
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteSetSize();
  }
}
