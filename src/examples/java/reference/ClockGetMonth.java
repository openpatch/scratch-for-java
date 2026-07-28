package reference;
import org.openpatch.scratch.*;


public class ClockGetMonth {
  public ClockGetMonth() {
    // January is 1, December is 12.
    System.out.println("The month is " + Clock.getMonth() + ".");
  }

  public static void main(String[] args) {
    new ClockGetMonth();
  }
}
