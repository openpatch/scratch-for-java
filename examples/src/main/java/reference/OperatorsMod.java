package reference;
import org.openpatch.scratch.*;


public class OperatorsMod {
  public OperatorsMod() {
    // The remainder of a division. Counting 0, 1, 2, 0, 1, 2, ... is what mod is
    // most often used for.
    for (int i = 0; i < 7; i++) {
      System.out.println(i + " mod 3 = " + Operators.mod(i, 3));
    }
  }

  public static void main(String[] args) {
    new OperatorsMod();
  }
}
