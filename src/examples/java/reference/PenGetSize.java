package reference;
import org.openpatch.scratch.*;


public class PenGetSize {
  public PenGetSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setSize(12);
    System.out.println("The line is " + myPen.getSize() + " pixels thick.");
  }

  public static void main(String[] args) {
    new PenGetSize();
  }
}
