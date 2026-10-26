package reference;
import org.openpatch.scratch.*;


public class Vector2GetY {
  public Vector2GetY() {
    Vector2 myVector = new Vector2(60, 80);
    System.out.println("The y-coordinate of " + myVector + " is " + myVector.getY() + ".");
  }

  public static void main(String[] args) {
    new Vector2GetY();
  }
}
