import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.recorder.GifRecorder;

public class PenSetPosition {
  public PenSetPosition() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myPen.setSize(10);
    while (myStage.getTimer().forMillis(3000)) {
      myPen.changeColor(10);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2), 45);
      myPen.up();
      myStage.wait(200);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new PenSetPosition();
  }
}
