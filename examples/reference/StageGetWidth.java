import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetWidth {
  public StageGetWidth() {
    Stage myStage = new Stage(600, 240);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myStage.display("Width: " + myStage.getWidth());
    myStage.wait(500);
    recorder.snapshot();
    myStage.wait(500);
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetWidth();
  }
}
