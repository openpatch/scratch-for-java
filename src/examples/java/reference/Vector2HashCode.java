package reference;
import org.openpatch.scratch.*;


public class Vector2HashCode {
  public Vector2HashCode() {
    Vector2 myVector = new Vector2(60, 80);
    Vector2 sameVector = new Vector2(60, 80);
    // Two vectors that are equal always have the same hash code, which is what
    // lets them be used as keys in a HashMap.
    System.out.println(myVector.hashCode() + " and " + sameVector.hashCode());
  }

  public static void main(String[] args) {
    new Vector2HashCode();
  }
}
