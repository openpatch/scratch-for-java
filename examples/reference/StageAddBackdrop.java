import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class StageAddBackdrop {
  public StageAddBackdrop() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "assets/background_forest.png", true);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myStage.wait(1000);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageAddBackdrop();
  }
}
