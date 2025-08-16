package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetCurrentBackdropIndex {
  public StageGetCurrentBackdropIndex() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "assets/background_forest.png");
    myStage.addBackdrop("sea", "assets/background_sea.png");
    myStage.display("Index: " + myStage.getCurrentBackdropIndex());
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.nextBackdrop();
    myStage.display("Index: " + myStage.getCurrentBackdropIndex());
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetCurrentBackdropIndex();
  }
}
