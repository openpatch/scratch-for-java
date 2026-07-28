package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerAfterMillis {
  public TimerAfterMillis() {
    Stage myStage = new Stage(600, 240);
    Timer myTimer = new Timer();
    while (true) {
      myStage.display("Waiting...");
      if (myTimer.afterMillis(3000)) {
        break;
      }
    }
  }

  public static void main(String[] args) {
    new TimerAfterMillis();
  }
}
