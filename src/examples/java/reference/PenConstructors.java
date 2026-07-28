package reference;
import org.openpatch.scratch.*;


public class PenConstructors {
  public PenConstructors() {
    Stage myStage = new Stage(600, 240);
    // A pen of its own, which draws wherever it is told to go.
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);
    myPen.down();
    while (true) {
      myPen.goToRandomPosition();
      myStage.wait(300);
    }
  }

  public static void main(String[] args) {
    new PenConstructors();
  }
}
