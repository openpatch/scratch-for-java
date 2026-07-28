package reference;
import org.openpatch.scratch.*;


public class Vector2UnitVector {
  public Vector2UnitVector() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(120, 90);
    // The same direction, but exactly one pixel long - too short to see, so it
    // is drawn 50 times over.
    Vector2 unit = myVector.unitVector();
    Vector2 fifty = unit.multiply(50);

    // red: the vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    // blue: its unit vector, 50x
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(fifty.getX(), fifty.getY());
    myPen.up();
    System.out.println(myVector + " has the direction " + unit);
    System.out.println("which is " + unit.length() + " long.");
  }

  public static void main(String[] args) {
    new Vector2UnitVector();
  }
}
