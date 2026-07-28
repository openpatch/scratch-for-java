package reference;
import org.openpatch.scratch.*;


public class Vector2LengthSq {
  public Vector2LengthSq() {
    Vector2 myVector = new Vector2(60, 80);
    // lengthSq() skips the square root, so it is the faster way to find out
    // which of two vectors is the longer one.
    System.out.println("length " + myVector.length() + ", squared " + myVector.lengthSq());
  }

  public static void main(String[] args) {
    new Vector2LengthSq();
  }
}
