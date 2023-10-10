import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGoToFrontLayer {
  public SpriteGoToFrontLayer() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);
    myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
    myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
    myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
    myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
    myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(1000);
    gamma.goToFrontLayer();
    myStage.wait(1000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGoToFrontLayer();
  }
}
