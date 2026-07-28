package reference;
import org.openpatch.scratch.*;


public class OperatorsSinOf {
  public OperatorsSinOf() {
    // All the angles are in degrees, just like the directions of a sprite.
    System.out.println("sinOf(0) = " + Operators.sinOf(0));
    System.out.println("sinOf(90) = " + Operators.sinOf(90));
    System.out.println("sinOf(180) = " + Operators.round(Operators.sinOf(180), 3));
  }

  public static void main(String[] args) {
    new OperatorsSinOf();
  }
}
