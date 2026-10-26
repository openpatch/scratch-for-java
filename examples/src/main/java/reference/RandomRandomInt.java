package reference;
import org.openpatch.scratch.*;


public class RandomRandomInt {
  public RandomRandomInt() {
    // Rolling a die ten times. Both ends are included, so a 1 and a 6 can come up.
    for (int i = 0; i < 10; i++) {
      System.out.println(Random.randomInt(1, 6));
    }
  }

  public static void main(String[] args) {
    new RandomRandomInt();
  }
}
