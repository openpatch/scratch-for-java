package reference;
import org.openpatch.scratch.*;


public class Vector2NormalVector {
  public Vector2NormalVector() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(120, 60);
    Vector2 normal = myVector.normalVector();

    // red: the vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    // blue: at right angles to it
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(normal.getX(), normal.getY());
    myPen.up();
    System.out.println(myVector + " and " + normal + " meet at a right angle.");
  }

  public static void main(String[] args) {
    new Vector2NormalVector();
  }
}
