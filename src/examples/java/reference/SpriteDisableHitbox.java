package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteDisableHitbox {
  public SpriteDisableHitbox() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    
    gamma.setPosition(-20, 0);
    zeta.setPosition(20, 0);
    
    myStage.add(gamma);
    myStage.add(zeta);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Check collision before disabling hitbox
    gamma.say("Touching: " + gamma.isTouchingSprite(zeta));
    myStage.wait(1500);
    
    // Disable hitbox
    gamma.disableHitbox();
    gamma.say("Hitbox disabled");
    myStage.wait(1000);
    
    // Check collision after disabling
    gamma.say("Touching: " + gamma.isTouchingSprite(zeta));
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteDisableHitbox();
  }
}