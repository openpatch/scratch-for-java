package reference;
import org.openpatch.scratch.*;

public class PenEraseAll {
  public PenEraseAll() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.down();
    myPen.setSize(10);
    myPen.setPosition(120, 45);
    myPen.setColor(0, 255, 0);
    myPen.setPosition(50, 45);
    myPen.up();
    myPen.setPosition(50, 0);
    while (true) {
      if (myStage.isKeyPressed(KeyCode.SPACE)) {
        myPen.eraseAll();
      }
    }
  }

  public static void main(String[] args) {
    new PenEraseAll();
  }
}
