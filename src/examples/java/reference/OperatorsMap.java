package reference;
import org.openpatch.scratch.*;


public class OperatorsMap {
  public OperatorsMap() {
    // A value from one range translated into another: a score between 0 and 50
    // becomes a bar between 0 and 200 pixels wide.
    int score = 20;
    double width = Operators.map(score, 0, 50, 0, 200);
    System.out.println("A score of " + score + " out of 50 is a bar " + width + " pixels wide.");
  }

  public static void main(String[] args) {
    new OperatorsMap();
  }
}
