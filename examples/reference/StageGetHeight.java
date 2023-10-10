import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetHeight {
  public StageGetHeight() {
    Stage myStage = new Stage(600, 240);

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myStage.display("Height: " + myStage.getHeight());
    myStage.wait(500);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetHeight();
  }
}
