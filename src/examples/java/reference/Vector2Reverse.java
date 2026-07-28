package reference;
import org.openpatch.scratch.*;


public class Vector2Reverse {
  public Vector2Reverse() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 myVector = new Vector2(120, 60);
    Vector2 backwards = myVector.reverse();

    // red: the vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(myVector.getX(), myVector.getY());
    myPen.up();
    // blue: the other way round
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(backwards.getX(), backwards.getY());
    myPen.up();
    System.out.println(myVector + " reversed is " + backwards);
  }

  public static void main(String[] args) {
    new Vector2Reverse();
  }
}
