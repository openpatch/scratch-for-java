package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageSetTransparency {
  public StageSetTransparency() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "assets/background_forest.png");
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    myStage.setTransparency(50);
    ;
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageSetTransparency();
  }
}
