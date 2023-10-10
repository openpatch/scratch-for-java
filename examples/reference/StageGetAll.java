import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetAll {
  public StageGetAll() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new Pen());
    myStage.add(new Sprite());
    myStage.add(new Sprite());

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myStage.display("All: " + myStage.getAll().size());
    myStage.wait(2000);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetAll();
  }
}
