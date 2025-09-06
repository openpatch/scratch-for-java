package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetTouchingSprites {
  public SpriteGetTouchingSprites() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta1 = new Sprite("zeta1", "assets/zeta_green_badge.png");
    Sprite zeta2 = new Sprite("zeta2", "assets/zeta_yellow_badge.png");
    
    gamma.setPosition(0, 0);
    zeta1.setPosition(0, 0);    // Overlapping with gamma
    zeta2.setPosition(0, 0);    // Overlapping with gamma
    
    myStage.add(gamma);
    myStage.add(zeta1);
    myStage.add(zeta2);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Get all touching sprites
    var touchingSprites = gamma.getTouchingSprites(Sprite.class);
    gamma.say("Touching " + touchingSprites.size() + " sprites");
    
    myStage.wait(3000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetTouchingSprites();
  }
}