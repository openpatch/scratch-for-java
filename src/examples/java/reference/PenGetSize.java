package reference;
import org.openpatch.scratch.*;


public class PenGetSize {
  public PenGetSize() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(12);
    System.out.println("The line is " + myPen.getSize() + " pixels thick.");
  }

  public static void main(String[] args) {
    new PenGetSize();
  }
}
