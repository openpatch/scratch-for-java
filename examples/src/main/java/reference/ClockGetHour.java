package reference;
import org.openpatch.scratch.*;


public class ClockGetHour {
  public ClockGetHour() {
    System.out.println("The hour is " + Clock.getHour() + ".");
  }

  public static void main(String[] args) {
    new ClockGetHour();
  }
}
