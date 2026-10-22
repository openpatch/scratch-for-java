package reference;
import org.openpatch.scratch.*;


public class ClockGetDayOfWeek {
  public ClockGetDayOfWeek() {
    // Monday is 1, Sunday is 7.
    System.out.println("The day of the week is " + Clock.getDayOfWeek() + ".");
  }

  public static void main(String[] args) {
    new ClockGetDayOfWeek();
  }
}
