package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageTimer {
  public StageTimer() {
    Stage myStage = new Stage(600, 240);
    myStage.addTimer("identityChange");

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(6000)) {
      if (myStage.getTimer("identityChange").everyMillis(1000)) {
        myStage.changeColor(40);
      }
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageTimer();
  }
}
