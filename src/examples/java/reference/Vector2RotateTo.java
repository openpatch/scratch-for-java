package reference;
import org.openpatch.scratch.*;


public class Vector2RotateTo {
  public Vector2RotateTo() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(120, 0);
    // rotateTo() keeps the length and sets the direction outright.
    Vector2 turned = myVector.rotateTo(135);

    // red: the vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    // blue: pointing at 135 degrees
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(turned.getX(), turned.getY());
    myPen.up();
    System.out.println("Both are " + myVector.length() + " long.");
    System.out.println(turned + " points at " + turned.angle() + " degrees.");
  }

  public static void main(String[] args) {
    new Vector2RotateTo();
  }
}
