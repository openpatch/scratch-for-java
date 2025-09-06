package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetHitbox {
  public SpriteGetHitbox() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Get the hitbox and show its bounds
    var hitbox = mySprite.getHitbox();
    var bounds = hitbox.getBounds();
    mySprite.say("Hitbox: " + (int)bounds.width() + "x" + (int)bounds.height());
    
    myStage.wait(3000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetHitbox();
  }
}