package reference;
import org.openpatch.scratch.*;


public class OperatorsRound {
  public OperatorsRound() {
    System.out.println("round(2.5) = " + Operators.round(2.5));
    System.out.println("round(2.4) = " + Operators.round(2.4));
    System.out.println("round(3.14159, 2) = " + Operators.round(3.14159, 2));
  }

  public static void main(String[] args) {
    new OperatorsRound();
  }
}
