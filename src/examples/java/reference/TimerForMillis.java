package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerForMillis {
  public TimerForMillis() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Timer myTimer = new Timer();
    myStage.display("Start!");
    myStage.wait(500);
    while (myTimer.forMillis(1000)) {
      myStage.display("Running...");
    }
    ;
    myStage.display("Stop!");
    myStage.wait(500);
  }

  public static void main(String[] args) {
    new TimerForMillis();
  }
}
