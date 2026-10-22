package reference;
import org.openpatch.scratch.*;


public class Vector2Angle {
  public Vector2Angle() {
    Vector2 myVector = new Vector2(100, 100);
    System.out.println("The vector " + myVector + " points at " + myVector.angle() + " degrees.");
  }

  public static void main(String[] args) {
    new Vector2Angle();
  }
}
