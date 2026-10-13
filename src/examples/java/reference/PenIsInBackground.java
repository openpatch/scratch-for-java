package reference;
import org.openpatch.scratch.*;


public class PenIsInBackground {
  public PenIsInBackground() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);

    System.out.println("draws behind the sprites: " + myPen.isInBackground());
    myPen.goToBackground();
    System.out.println("and now: " + myPen.isInBackground());
  }

  public static void main(String[] args) {
    new PenIsInBackground();
  }
}
