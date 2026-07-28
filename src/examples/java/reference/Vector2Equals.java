package reference;
import org.openpatch.scratch.*;


public class Vector2Equals {
  public Vector2Equals() {
    Vector2 myVector = new Vector2(60, 80);
    Vector2 sameVector = new Vector2(60, 80);
    Vector2 otherVector = new Vector2(80, 60);
    System.out.println("same values: " + myVector.equals(sameVector));
    System.out.println("other values: " + myVector.equals(otherVector));
  }

  public static void main(String[] args) {
    new Vector2Equals();
  }
}
