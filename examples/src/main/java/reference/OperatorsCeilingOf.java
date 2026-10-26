package reference;
import org.openpatch.scratch.*;


public class OperatorsCeilingOf {
  public OperatorsCeilingOf() {
    System.out.println("ceilingOf(2.1) = " + Operators.ceilingOf(2.1));
    System.out.println("ceilingOf(-2.9) = " + Operators.ceilingOf(-2.9));
  }

  public static void main(String[] args) {
    new OperatorsCeilingOf();
  }
}
