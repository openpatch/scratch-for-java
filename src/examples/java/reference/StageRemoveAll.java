package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageRemoveAll {
  public StageRemoveAll() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    gamma.changeX(20);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    zeta.changeX(-20);
    myStage.add(zeta);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.removeAll();
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageRemoveAll();
  }
}
