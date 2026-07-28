package reference;
import org.openpatch.scratch.*;


public class ClockGetMillisecond {
  public ClockGetMillisecond() {
    System.out.println("The millisecond is " + Clock.getMillisecond() + ".");
  }

  public static void main(String[] args) {
    new ClockGetMillisecond();
  }
}
