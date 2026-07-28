package reference;
import org.openpatch.scratch.*;

public class PenUp {
  public PenUp() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(6);

    // With the pen down every move draws a line. Lifting it up moves the pen
    // without leaving anything behind, which is how a drawing gets a gap.
    while (true) {
      myPen.setPosition(-200, 0);
      myPen.down();
      myPen.setPosition(-60, 0);
      myPen.up();

      myPen.setPosition(60, 0);
      myPen.down();
      myPen.setPosition(200, 0);
      myPen.up();

      myStage.wait(1500);
      myStage.eraseAll();
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new PenUp();
  }
}
