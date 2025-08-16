package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageSetColor {
  public StageSetColor() {
    Stage myStage = new Stage(600, 240);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.setColor(200);
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageSetColor();
  }
}
