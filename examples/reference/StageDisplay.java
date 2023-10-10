import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageDisplay {
  public StageDisplay() {
    Stage myStage = new Stage(600, 240);
    myStage.display("Welcome! This is a longer text with auto line breaks! So if you write a long sentence it should wrap to the next line and the height of the box should increase.", 2000);
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageDisplay();
  }
}
