import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageRemove {
  public StageRemove() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.remove(gamma);
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageRemove();
  }
}
