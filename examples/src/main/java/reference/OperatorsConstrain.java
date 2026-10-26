package reference;
import org.openpatch.scratch.*;


public class OperatorsConstrain {
  public OperatorsConstrain() {
    // Keeping a value inside a range: anything below the low end becomes the low
    // end, anything above the high end becomes the high end.
    System.out.println("-30 becomes " + Operators.constrain(-30, 0, 100));
    System.out.println("42 stays " + Operators.constrain(42, 0, 100));
    System.out.println("180 becomes " + Operators.constrain(180, 0, 100));
  }

  public static void main(String[] args) {
    new OperatorsConstrain();
  }
}
