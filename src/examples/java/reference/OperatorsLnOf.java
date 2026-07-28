package reference;
import org.openpatch.scratch.*;


public class OperatorsLnOf {
  public OperatorsLnOf() {
    System.out.println("lnOf(1) = " + Operators.lnOf(1));
    System.out.println("lnOf(10) = " + Operators.round(Operators.lnOf(10), 4));
  }

  public static void main(String[] args) {
    new OperatorsLnOf();
  }
}
