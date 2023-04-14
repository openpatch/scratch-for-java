import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenChangeColor {
  public PenChangeColor() {
    Stage myStage = new Stage(254, 100);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    myPen.setSize(10);
    while (myStage.getTimer().forMillis(3000)) {
      myPen.changeColor(10);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(0, myStage.getWidth()), 45);
      myPen.up();
      myStage.wait(200);
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new PenChangeColor();
  }
}
