import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenDown {
  public PenDown() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myPen.down();
    myPen.setSize(10);
    myPen.setPosition(120, 45);
    myPen.setColor(0, 255, 0);
    myStage.wait(500);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenDown();
  }
}
