package reference;
import org.openpatch.scratch.*;

public class ClockGetCurrentTime {
  public ClockGetCurrentTime() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      int year = Clock.getYear();
      int month = Clock.getMonth();
      int day = Clock.getDay();
      int weekday = Clock.getDayOfWeek();
      int hour = Clock.getHour();
      int minute = Clock.getMinute();
      int second = Clock.getSecond();
      int millisecond = Clock.getMillisecond();
      int daysSince2000 = Clock.getDaysSince2000();
      myStage.display(
          hour
              + ":"
              + minute
              + ":"
              + second
              + ":"
              + millisecond
              + " "
              + year
              + "-"
              + month
              + "-"
              + day
              + " ("
              + weekday
              + ")"
              + " "
              + daysSince2000);
    }
  }

  public static void main(String[] args) {
    new ClockGetCurrentTime();
  }
}
