package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;

public class PenSetSize {
  public PenSetSize() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(10);
    while (true) {
      myPen.down();
      if (myStage.getTimer().everyMillis(500)) {
        myPen.setSize(myStage.pickRandom(5, 20));
      }
      myPen.goToRandomPosition();
      myPen.up();
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new PenSetSize();
  }
}
