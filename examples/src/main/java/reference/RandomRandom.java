package reference;
import org.openpatch.scratch.*;


public class RandomRandom {
  public RandomRandom() {
    System.out.println("random() gives " + Random.random());
    System.out.println("random(10) gives " + Random.random(10));
    System.out.println("random(5, 10) gives " + Random.random(5, 10));
  }

  public static void main(String[] args) {
    new RandomRandom();
  }
}
