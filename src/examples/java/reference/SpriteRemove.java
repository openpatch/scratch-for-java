package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteRemove {
  public SpriteRemove() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    
    gamma.setPosition(-100, 0);
    zeta.setPosition(100, 0);
    
    myStage.add(gamma);
    myStage.add(zeta);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    myStage.wait(1500);
    
    // Remove gamma sprite from stage
    gamma.remove();
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteRemove();
  }
}