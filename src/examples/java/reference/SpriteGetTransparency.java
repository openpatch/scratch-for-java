package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetTransparency {
  public SpriteGetTransparency() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    
    // Set transparency first
    mySprite.setTransparency(180);
    
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    mySprite.say("Transparency: " + (int) mySprite.getTransparency());
    recorder.start();
    myStage.wait(3000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetTransparency();
  }
}