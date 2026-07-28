package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;

public class PenSetPosition {
  public PenSetPosition() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(10);
    while (true) {
      myPen.changeColor(10);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2), 45);
      myPen.up();
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new PenSetPosition();
  }
}
