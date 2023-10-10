import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenChangeSize {
  public PenChangeSize() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      myPen.changeSize(1);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(0, myStage.getWidth()), 45);
      myPen.up();
      myStage.wait(200);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenChangeSize();
  }
}
