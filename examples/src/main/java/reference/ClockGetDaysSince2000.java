package reference;
import org.openpatch.scratch.*;


public class ClockGetDaysSince2000 {
  public ClockGetDaysSince2000() {
    // The days since the 1st of January 2000, the way Scratch counts them.
    System.out.println(Clock.getDaysSince2000() + " days have passed since 2000.");
  }

  public static void main(String[] args) {
    new ClockGetDaysSince2000();
  }
}
