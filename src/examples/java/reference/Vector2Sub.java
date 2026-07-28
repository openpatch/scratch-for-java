package reference;
import org.openpatch.scratch.*;


public class Vector2Sub {
  public Vector2Sub() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 target = new Vector2(120, 60);
    Vector2 position = new Vector2(-60, -40);
    // From one point to another: the vector that has to be walked.
    Vector2 difference = target.sub(position);

    // red: where to go
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(target.getX(), target.getY());
    myPen.up();
    // green: where we are
    myPen.setColor(85);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(position.getX(), position.getY());
    myPen.up();
    // blue: the way from one to the other
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(difference.getX(), difference.getY());
    myPen.up();
    System.out.println(target + " - " + position + " = " + difference);
  }

  public static void main(String[] args) {
    new Vector2Sub();
  }
}
