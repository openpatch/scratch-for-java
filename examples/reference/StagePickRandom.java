import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StagePickRandom {
  public StagePickRandom() {
    Stage myStage = new Stage(600, 240);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      int random = myStage.pickRandom(0, 100);
      myStage.display("Random: " + random);
      myStage.wait(500);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StagePickRandom();
  }
}
