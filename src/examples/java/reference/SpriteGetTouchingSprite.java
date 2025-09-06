package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetTouchingSprite {
  public SpriteGetTouchingSprite() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    
    gamma.setPosition(-50, 0);
    zeta.setPosition(50, 0);
    
    myStage.add(gamma);
    myStage.add(zeta);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    gamma.say("Moving to touch...");
    myStage.wait(1000);
    
    // Move gamma to touch zeta
    gamma.setPosition(50, 0);
    
    // Get the touching sprite
    Sprite touching = gamma.getTouchingSprite(Sprite.class);
    if (touching != null) {
      gamma.say("Touching sprite found!");
    } else {
      gamma.say("No sprite touching");
    }
    
    myStage.wait(2000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetTouchingSprite();
  }
}