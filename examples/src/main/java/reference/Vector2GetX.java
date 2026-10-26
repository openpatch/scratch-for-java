package reference;
import org.openpatch.scratch.*;


public class Vector2GetX {
  public Vector2GetX() {
    Vector2 myVector = new Vector2(60, 80);
    System.out.println("The x-coordinate of " + myVector + " is " + myVector.getX() + ".");
  }

  public static void main(String[] args) {
    new Vector2GetX();
  }
}
