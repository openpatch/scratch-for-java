package reference;
import org.openpatch.scratch.*;


public class ClockGetMinute {
  public ClockGetMinute() {
    System.out.println("The minute is " + Clock.getMinute() + ".");
  }

  public static void main(String[] args) {
    new ClockGetMinute();
  }
}
