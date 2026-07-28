package reference;
import org.openpatch.scratch.*;


public class ClockGetSecond {
  public ClockGetSecond() {
    System.out.println("The second is " + Clock.getSecond() + ".");
  }

  public static void main(String[] args) {
    new ClockGetSecond();
  }
}
