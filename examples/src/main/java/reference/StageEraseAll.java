package reference;
import org.openpatch.scratch.*;


public class StageEraseAll {
  public StageEraseAll() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);
    myPen.down();
    // Draws a while, then wipes everything the pens and stamps have left behind.
    while (true) {
      myPen.goToRandomPosition();
      myStage.waitSeconds(0.2);
      if (myStage.getTimer().every(3)) {
        myStage.eraseAll();
      }
    }
  }

  public static void main(String[] args) {
    new StageEraseAll();
  }
}
