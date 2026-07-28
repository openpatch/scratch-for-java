package reference;
import org.openpatch.scratch.*;


public class Vector2Distance {
  public Vector2Distance() {
    Vector2 here = new Vector2(-60, -40);
    Vector2 there = new Vector2(60, 40);
    System.out.println("The two points are " + here.distance(there) + " pixels apart.");
  }

  public static void main(String[] args) {
    new Vector2Distance();
  }
}
