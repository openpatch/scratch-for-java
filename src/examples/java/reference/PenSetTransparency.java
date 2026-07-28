package reference;
import org.openpatch.scratch.*;

public class PenSetTransparency {
  public PenSetTransparency() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.down();
    myPen.setSize(50);
    myPen.setTransparency(20);
    myPen.setPosition(0, 0);
    myPen.up();
    while (true) {}
  }

  public static void main(String[] args) {
    new PenSetTransparency();
  }
}
