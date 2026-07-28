package reference;
import org.openpatch.scratch.*;


public class RandomRandomVector2 {
  public RandomRandomVector2() {
    // A vector of length 1 pointing in a random direction - a random heading,
    // not a random point.
    for (int i = 0; i < 5; i++) {
      Vector2 direction = Random.randomVector2();
      System.out.println(direction + " points at " + direction.angle() + " degrees.");
    }
  }

  public static void main(String[] args) {
    new RandomRandomVector2();
  }
}
