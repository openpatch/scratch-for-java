package reference;
import org.openpatch.scratch.*;


public class OperatorsMax {
  public OperatorsMax() {
    System.out.println("max(3, 8, 5) = " + Operators.max(3, 8, 5));
    System.out.println("max(2.5, -1.5) = " + Operators.max(2.5, -1.5));
  }

  public static void main(String[] args) {
    new OperatorsMax();
  }
}
