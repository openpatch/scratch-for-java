package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetText {
  public SpriteGetText() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Show that we can access the text object
    var text = mySprite.getText();
    mySprite.say("Text object: " + text.getClass().getSimpleName());
    myStage.wait(3000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetText();
  }
}