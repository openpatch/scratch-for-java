package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenSetTransparency {
  public PenSetTransparency() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myPen.down();
    myPen.setSize(50);
    myPen.setTransparency(20);
    myPen.setPosition(0, 0);
    myPen.up();
    while (myStage.getTimer().forMillis(3000)) {}
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenSetTransparency();
  }
}
