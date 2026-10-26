package reference;
import org.openpatch.scratch.*;


public class PenGoToMousePointer {
  public PenGoToMousePointer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(4);
    myPen.down();
    // Drawing follows the mouse. Click into the stage first.
    while (true) {
      myPen.goToMousePointer();
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new PenGoToMousePointer();
  }
}
