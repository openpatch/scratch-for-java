package reference;
import org.openpatch.scratch.*;


public class PenStamp {
  public PenStamp() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(30);
    // A stamp leaves one round mark where the pen stands, without drawing a
    // line to get there.
    while (true) {
      myPen.goToRandomPosition();
      myPen.changeColor(20);
      myPen.stamp();
      myStage.waitSeconds(0.3);
    }
  }

  public static void main(String[] args) {
    new PenStamp();
  }
}
