package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetMouse {
  public StageGetMouse() {
    Stage myStage = new Stage(600, 240);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      var mouseX = myStage.getMouseX();
      var mouseY = myStage.getMouseY();

      myStage.display("X: " + mouseX + " Y: " + mouseY);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageGetMouse();
  }
}
