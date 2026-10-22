package reference;
import org.openpatch.scratch.*;


public class OperatorsAll {
  public OperatorsAll() {
    // Everything in Operators is static, so there is nothing to build first.
    double distance = Operators.sqrtOf(3 * 3 + 4 * 4);
    System.out.println("The distance is " + Operators.round(distance, 2) + ".");
    System.out.println("The largest of the three is " + Operators.max(5, 4, 3) + ".");
  }

  public static void main(String[] args) {
    new OperatorsAll();
  }
}
