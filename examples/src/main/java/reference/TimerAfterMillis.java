package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerAfterMillis {
  public TimerAfterMillis() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Timer myTimer = new Timer();
    while (true) {
      myStage.display("Waiting...");
      if (myTimer.after(3)) {
        break;
      }
    }
  }

  public static void main(String[] args) {
    new TimerAfterMillis();
  }
}
