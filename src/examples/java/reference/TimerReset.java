package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerReset {
  public TimerReset() {
    Stage myStage = new Stage(600, 240);
    Timer myTimer = new Timer();
    myStage.display("Start!");
    myStage.wait(500);
    while (myTimer.forMillis(1000)) {
      myStage.display("Running 1st...");
    }
    ;
    myTimer.reset();
    while (myTimer.forMillis(1000)) {
      myStage.display("Running 2nd...");
    }
    ;
    myStage.display("Stop!");
    myStage.wait(500);
  }

  public static void main(String[] args) {
    new TimerReset();
  }
}
