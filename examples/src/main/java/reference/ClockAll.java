package reference;
import org.openpatch.scratch.*;


public class ClockAll {
  public ClockAll() {
    // Every method in Clock is static, so there is nothing to build first.
    System.out.println(Clock.getDay() + "." + Clock.getMonth() + "." + Clock.getYear());
    System.out.println(Clock.getHour() + ":" + Clock.getMinute() + ":" + Clock.getSecond());
  }

  public static void main(String[] args) {
    new ClockAll();
  }
}
