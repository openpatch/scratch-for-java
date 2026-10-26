package reference;
import org.openpatch.scratch.*;


public class OperatorsTanOf {
  public OperatorsTanOf() {
    System.out.println("tanOf(0) = " + Operators.tanOf(0));
    System.out.println("tanOf(45) = " + Operators.round(Operators.tanOf(45), 3));
  }

  public static void main(String[] args) {
    new OperatorsTanOf();
  }
}
