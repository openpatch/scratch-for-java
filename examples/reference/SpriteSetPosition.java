import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetPosition {
  public SpriteSetPosition() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "assets/slime.png");
    myStage.add(mySprite);

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      int x = myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2);
      int y = myStage.pickRandom(-myStage.getHeight() / 2, myStage.getHeight() / 2);

      mySprite.setPosition(x, y);
      myStage.wait(200);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetPosition();
  }
}
