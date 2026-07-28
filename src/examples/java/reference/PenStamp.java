package reference;
import org.openpatch.scratch.*;


public class PenStamp {
  public PenStamp() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(30);
    // A stamp leaves one round mark where the pen stands, without drawing a
    // line to get there.
    while (true) {
      myPen.goToRandomPosition();
      myPen.changeColor(20);
      myPen.stamp();
      myStage.wait(300);
    }
  }

  public static void main(String[] args) {
    new PenStamp();
  }
}
