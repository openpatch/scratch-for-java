package reference;
import org.openpatch.scratch.*;


public class Vector2RotateBy {
  public Vector2RotateBy() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(120, 0);
    Vector2 turned = myVector.rotateBy(45);

    // red: the vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    // blue: turned by 45 degrees
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(turned.getX(), turned.getY());
    myPen.up();
    System.out.println(myVector + " turned by 45 degrees is " + turned);
  }

  public static void main(String[] args) {
    new Vector2RotateBy();
  }
}
