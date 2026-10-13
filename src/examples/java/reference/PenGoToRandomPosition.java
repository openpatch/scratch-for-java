package reference;
import org.openpatch.scratch.*;


public class PenGoToRandomPosition {
  public PenGoToRandomPosition() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
