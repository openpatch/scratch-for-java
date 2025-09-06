package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGoToSprite {
  public SpriteGoToSprite() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    
    myStage.add(gamma);
    myStage.add(zeta);
    
    // Position zeta at different locations
    zeta.setPosition(100, 50);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    myStage.wait(500);
    gamma.goToSprite(zeta); // Move gamma to zeta's position
    myStage.wait(1000);
    
    zeta.setPosition(-100, -50);
    myStage.wait(500);
    gamma.goToSprite(zeta); // Move gamma to zeta's new position
    myStage.wait(1000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGoToSprite();
  }
}