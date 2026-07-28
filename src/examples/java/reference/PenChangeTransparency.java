package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;

public class PenChangeTransparency {
  public PenChangeTransparency() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(10);
    while (true) {
      myPen.changeTransparency(10);
      myPen.down();
      myPen.goToRandomPosition();
      myPen.up();
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new PenChangeTransparency();
  }
}
