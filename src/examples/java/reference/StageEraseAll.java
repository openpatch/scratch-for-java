package reference;
import org.openpatch.scratch.*;


public class StageEraseAll {
  public StageEraseAll() {
    Stage myStage = new Stage(600, 240);

    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);
    myPen.down();
    // Draws a while, then wipes everything the pens and stamps have left behind.
    while (true) {
      myPen.goToRandomPosition();
      myStage.wait(200);
      if (myStage.getTimer().everyMillis(3000)) {
        myStage.eraseAll();
      }
    }
  }

  public static void main(String[] args) {
    new StageEraseAll();
  }
}
