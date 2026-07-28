package reference;
import org.openpatch.scratch.*;


public class Vector2Multiply {
  public Vector2Multiply() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(40, 30);
    Vector2 twiceAsLong = myVector.multiply(2);

    // blue: the longer one, drawn first
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(twiceAsLong.getX(), twiceAsLong.getY());
    myPen.up();
    // red: the original
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    System.out.println(myVector + " times 2 is " + twiceAsLong);
  }

  public static void main(String[] args) {
    new Vector2Multiply();
  }
}
