package reference;
import org.openpatch.scratch.*;


public class TimerMillis {
  public TimerMillis() {
    // The milliseconds since the program started - a number to subtract from a
    // later one, not a time of day.
    int start = Timer.millis();
    System.out.println("The program has been running for " + start + " milliseconds.");
  }

  public static void main(String[] args) {
    new TimerMillis();
  }
}
