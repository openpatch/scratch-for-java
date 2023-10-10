import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteAddCostume {
  public SpriteAddCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addCostume("yellow", "assets/zeta_yellow_badge.png");
    myStage.add(zeta);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(1000);
    recorder.stop();

    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteAddCostume();
  }
}
