package reference;
import org.openpatch.scratch.*;


public class OperatorsEToThePowerOf {
  public OperatorsEToThePowerOf() {
    System.out.println("eToThePowerOf(0) = " + Operators.eToThePowerOf(0));
    System.out.println("eToThePowerOf(1) = " + Operators.round(Operators.eToThePowerOf(1), 4));
  }

  public static void main(String[] args) {
    new OperatorsEToThePowerOf();
  }
}
