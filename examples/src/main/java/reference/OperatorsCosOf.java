package reference;
import org.openpatch.scratch.*;


public class OperatorsCosOf {
  public OperatorsCosOf() {
    System.out.println("cosOf(0) = " + Operators.cosOf(0));
    System.out.println("cosOf(90) = " + Operators.round(Operators.cosOf(90), 3));
    System.out.println("cosOf(180) = " + Operators.cosOf(180));
  }

  public static void main(String[] args) {
    new OperatorsCosOf();
  }
}
