package reference;
import org.openpatch.scratch.*;


public class PenGoToMousePointer {
  public PenGoToMousePointer() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(4);
    myPen.down();
    // Drawing follows the mouse. Click into the stage first.
    while (true) {
      myPen.goToMousePointer();
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new PenGoToMousePointer();
  }
}
