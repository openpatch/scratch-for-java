package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenEraseAll {
  public PenEraseAll() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    myPen.down();
    myPen.setSize(10);
    myPen.setPosition(120, 45);
    myPen.setColor(0, 255, 0);
    myPen.setPosition(50, 45);
    myPen.up();
    myPen.setPosition(50, 0);
    while (myStage.getTimer().forMillis(3000)) {
      if (myStage.isKeyPressed(KeyCode.VK_SPACE)) {
        myPen.eraseAll();
      }
    }
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenEraseAll();
  }
}
