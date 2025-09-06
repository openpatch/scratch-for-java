package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetPosition {
  public SpriteGetPosition() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    
    // Set a position first
    mySprite.setPosition(120, -80);
    
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    
    var position = mySprite.getPosition();
    mySprite.say("Position: (" + (int)position.getX() + ", " + (int)position.getY() + ")");
    
    recorder.start();
    myStage.wait(3000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetPosition();
  }
}