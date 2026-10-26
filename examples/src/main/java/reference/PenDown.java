package reference;
import org.openpatch.scratch.*;

public class PenDown {
  public PenDown() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.down();
    myPen.setSize(10);
    myPen.setPosition(120, 45);
    myPen.setColor(0, 255, 0);
    myStage.waitSeconds(0.5);
  }

  public static void main(String[] args) {
    new PenDown();
  }
}
