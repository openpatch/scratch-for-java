package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;
import org.openpatch.scratch.Window;

public class PenChangeTransparency {
  public PenChangeTransparency() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
