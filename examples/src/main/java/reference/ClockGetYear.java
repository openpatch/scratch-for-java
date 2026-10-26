package reference;
import org.openpatch.scratch.*;


public class ClockGetYear {
  public ClockGetYear() {
    System.out.println("The year is " + Clock.getYear() + ".");
  }

  public static void main(String[] args) {
    new ClockGetYear();
  }
}
