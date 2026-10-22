package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;
import org.openpatch.scratch.Window;

public class PenSetSize {
  public PenSetSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(10);
    while (true) {
      myPen.down();
      if (myStage.getTimer().every(0.5)) {
        myPen.setSize(myStage.pickRandom(5, 20));
      }
      myPen.goToRandomPosition();
      myPen.up();
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new PenSetSize();
  }
}
