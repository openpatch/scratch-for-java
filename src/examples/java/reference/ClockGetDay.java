package reference;
import org.openpatch.scratch.*;


public class ClockGetDay {
  public ClockGetDay() {
    System.out.println("Today is the " + Clock.getDay() + ". of the month.");
  }

  public static void main(String[] args) {
    new ClockGetDay();
  }
}
