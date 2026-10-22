package reference;
import org.openpatch.scratch.*;


public class Vector2Dot {
  public Vector2Dot() {
    Vector2 right = new Vector2(100, 0);
    Vector2 up = new Vector2(0, 100);
    // The dot product of two vectors at right angles to each other is 0.
    System.out.println("right . right = " + right.dot(right));
    System.out.println("right . up = " + right.dot(up));
  }

  public static void main(String[] args) {
    new Vector2Dot();
  }
}
