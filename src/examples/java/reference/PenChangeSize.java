package reference;
import org.openpatch.scratch.*;

public class PenChangeSize {
  public PenChangeSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    while (true) {
      myPen.changeSize(1);
      myPen.down();
      myPen.setPosition(myStage.pickRandom(0, myStage.getWidth()), 45);
      myPen.up();
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new PenChangeSize();
  }
}
