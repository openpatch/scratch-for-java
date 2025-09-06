package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetTint {
  public SpriteGetTint() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    
    // Set a tint first
    mySprite.setTint(255, 100, 100); // Reddish tint
    
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    
    var tint = mySprite.getTint();
    mySprite.say("Tint: R=" + (int)tint.getRed() + " G=" + (int)tint.getGreen() + " B=" + (int)tint.getBlue());
    
    recorder.start();
    myStage.wait(3000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetTint();
  }
}