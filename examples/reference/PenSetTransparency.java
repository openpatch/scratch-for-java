import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenSetTransparency {
  public PenSetTransparency() {
    Stage myStage = new Stage(254, 100);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    myPen.down();
    myPen.setSize(10);
    myPen.setTransparency(20);
    myPen.setPosition(120, 45);
    myStage.wait(500);
    recorder.snapshot();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new PenSetTransparency();
  }
}
