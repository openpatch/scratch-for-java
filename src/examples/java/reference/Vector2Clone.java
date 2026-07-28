package reference;
import org.openpatch.scratch.*;


public class Vector2Clone {
  public Vector2Clone() {
    Vector2 myVector = new Vector2(60, 80);
    Vector2 copy = myVector.clone();
    System.out.println("The copy " + copy + " equals the original: " + copy.equals(myVector));
  }

  public static void main(String[] args) {
    new Vector2Clone();
  }
}
