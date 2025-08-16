package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetCurrentBackdropName {
  public StageGetCurrentBackdropName() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "assets/background_forest.png");
    myStage.addBackdrop("sea", "assets/background_sea.png");
    myStage.display("Name: " + myStage.getCurrentBackdropName());
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.nextBackdrop();
    myStage.display("Name: " + myStage.getCurrentBackdropName());
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetCurrentBackdropName();
  }
}
