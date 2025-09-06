package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePointTowardsSprite {
  public SpritePointTowardsSprite() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    
    myStage.add(gamma);
    myStage.add(zeta);
    
    gamma.setPosition(-100, 0);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Move zeta to different positions and make gamma point towards it
    zeta.setPosition(100, 50);
    gamma.pointTowardsSprite(zeta);
    myStage.wait(1000);
    
    zeta.setPosition(100, -50);
    gamma.pointTowardsSprite(zeta);
    myStage.wait(1000);
    
    zeta.setPosition(-50, 80);
    gamma.pointTowardsSprite(zeta);
    myStage.wait(1000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePointTowardsSprite();
  }
}