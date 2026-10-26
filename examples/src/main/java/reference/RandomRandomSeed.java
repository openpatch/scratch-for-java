package reference;
import org.openpatch.scratch.*;


public class RandomRandomSeed {
  public RandomRandomSeed() {
    // The same seed gives the same numbers again, which is what makes a random
    // program testable.
    Random.randomSeed(42);
    System.out.println(Random.randomInt(100) + ", " + Random.randomInt(100));
    Random.randomSeed(42);
    System.out.println(Random.randomInt(100) + ", " + Random.randomInt(100));
  }

  public static void main(String[] args) {
    new RandomRandomSeed();
  }
}
