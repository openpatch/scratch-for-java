package reference;
import org.openpatch.scratch.*;


public class Vector2FromPolar {
  public Vector2FromPolar() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    // A vector of length 100 pointing 30 degrees away from the x-axis.
    Vector2 myVector = Vector2.fromPolar(100, 30);

    // the vector itself
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    System.out.println("fromPolar(100, 30) is " + myVector);
  }

  public static void main(String[] args) {
    new Vector2FromPolar();
  }
}
