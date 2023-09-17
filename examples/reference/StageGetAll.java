import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetAll {
  public StageGetAll() {
    Stage myStage = new Stage(254, 100);
    myStage.add(new Pen());
    myStage.add(new Pen());
    myStage.add(new Sprite());

    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    myStage.display("All: " + myStage.getAll().length);
    myStage.wait(2000);
    recorder.snapshot();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageGetAll();
  }
}
