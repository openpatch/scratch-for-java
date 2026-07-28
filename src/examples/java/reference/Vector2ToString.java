package reference;
import org.openpatch.scratch.*;


public class Vector2ToString {
  public Vector2ToString() {
    Vector2 myVector = new Vector2(60, 80);
    System.out.println("As a text this vector reads " + myVector.toString());
  }

  public static void main(String[] args) {
    new Vector2ToString();
  }
}
