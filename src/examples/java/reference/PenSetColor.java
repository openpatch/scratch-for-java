package reference;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenSetColor {
  public PenSetColor() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myPen.down();
    myPen.setSize(10);
    myPen.setColor(0, 255, 0);
    myPen.setPosition(120, 45);
    myStage.wait(1500);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenSetColor();
  }
}
