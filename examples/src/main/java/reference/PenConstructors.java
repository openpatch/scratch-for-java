package reference;
import org.openpatch.scratch.*;


public class PenConstructors {
  public PenConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    // A pen of its own, which draws wherever it is told to go.
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);
    myPen.down();
    while (true) {
      myPen.goToRandomPosition();
      myStage.waitSeconds(0.3);
    }
  }

  public static void main(String[] args) {
    new PenConstructors();
  }
}
