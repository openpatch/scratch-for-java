package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerIntervalMillis {
  public TimerIntervalMillis() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Timer myTimer = new Timer();
    while (myStage.getTimer().during(3)) {
      if (myTimer.interval(0.5)) {
        myStage.display("Interval 1");
      } else {
        myStage.display("Interval 2");
      }
    }
  }

  public static void main(String[] args) {
    new TimerIntervalMillis();
  }
}
