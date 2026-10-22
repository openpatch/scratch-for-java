package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;
import org.openpatch.scratch.Window;

public class PenSetPosition {
  public PenSetPosition() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(10);
    while (true) {
      myPen.changeColor(10);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2), 45);
      myPen.up();
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new PenSetPosition();
  }
}
