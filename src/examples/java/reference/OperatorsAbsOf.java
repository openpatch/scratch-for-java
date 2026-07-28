package reference;
import org.openpatch.scratch.*;


public class OperatorsAbsOf {
  public OperatorsAbsOf() {
    System.out.println("absOf(-7) = " + Operators.absOf(-7));
    System.out.println("absOf(2.5) = " + Operators.absOf(2.5));
    System.out.println("absOf(-2.5) = " + Operators.absOf(-2.5));
  }

  public static void main(String[] args) {
    new OperatorsAbsOf();
  }
}
