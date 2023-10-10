import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetDeltaTime {
  public StageGetDeltaTime() {
    Stage myStage = new Stage(600, 240);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      var dt = myStage.getDeltaTime();
      myStage.display("Delta Time: " + dt);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetDeltaTime();
  }
}
