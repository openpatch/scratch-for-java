package reference;
import org.openpatch.scratch.*;


public class PenGoToRandomPosition {
  public PenGoToRandomPosition() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(4);
    myPen.down();
    // A tangle of straight lines, one every third of a second.
    while (true) {
      myPen.goToRandomPosition();
      myStage.wait(300);
    }
  }

  public static void main(String[] args) {
    new PenGoToRandomPosition();
  }
}
