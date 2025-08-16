package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class StageAdd {
  public StageAdd() {
    Stage myStage = new Stage(600, 240);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myStage.add(new Sprite("cat", "assets/slime.png"));
    recorder.start();
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageAdd();
  }
}
