package reference;
import org.openpatch.scratch.*;


public class RandomAll {
  public RandomAll() {
    // Everything in Random is static, so there is nothing to build first.
    System.out.println("a number between 0 and 1: " + Random.random());
    System.out.println("a whole number up to 6: " + Random.randomInt(6));
    System.out.println("somewhere on the stage: " + Random.randomPosition());
  }

  public static void main(String[] args) {
    new RandomAll();
  }
}
