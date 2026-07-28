package reference;
import org.openpatch.scratch.*;


public class OperatorsMin {
  public OperatorsMin() {
    System.out.println("min(3, 8, 5) = " + Operators.min(3, 8, 5));
    System.out.println("min(2.5, -1.5) = " + Operators.min(2.5, -1.5));
  }

  public static void main(String[] args) {
    new OperatorsMin();
  }
}
