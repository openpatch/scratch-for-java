package reference;
import org.openpatch.scratch.*;


public class Vector2Constructors {
  public Vector2Constructors() {
    Vector2 nullVector = new Vector2();
    Vector2 point = new Vector2(60, 80);
    Vector2 copy = new Vector2(point);
    System.out.println(nullVector + ", " + point + " and its copy " + copy);
  }

  public static void main(String[] args) {
    new Vector2Constructors();
  }
}
