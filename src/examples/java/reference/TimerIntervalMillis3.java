package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerIntervalMillis3 {
  public TimerIntervalMillis3() {
    Stage myStage = new Stage(600, 240);
    Timer myTimer = new Timer();
    while (myStage.getTimer().forMillis(3000)) {
      if (myTimer.intervalMillis(500, true)) {
        myStage.display("Interval 1");
      } else {
        myStage.display("Interval 2");
      }
    }
  }

  public static void main(String[] args) {
    new TimerIntervalMillis3();
  }
}
