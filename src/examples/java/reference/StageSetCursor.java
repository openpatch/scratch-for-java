package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageSetCursor {

  public StageSetCursor() {
    Stage myStage = new Stage(600, 240);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(5000)) {
      if (myStage.getMouseX() < 0) {
        myStage.setCursor("assets/cursor_hand.png");
      } else {
        myStage.setCursor("assets/cursor_sword.png", 30, 30);
      }
      myStage.display("Mouse: " + myStage.getMouseX() + ", " + myStage.getMouseY());
      myStage.wait(16);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageSetCursor();
  }
}
