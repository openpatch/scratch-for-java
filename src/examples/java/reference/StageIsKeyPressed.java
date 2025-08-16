package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageIsKeyPressed {
  public StageIsKeyPressed() {
    Stage myStage = new Stage(600, 240);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      myStage.display("Space pressed? " + myStage.isKeyPressed(KeyCode.VK_SPACE));
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageIsKeyPressed();
  }
}
