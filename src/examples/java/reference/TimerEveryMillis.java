package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Timer;

public class TimerEveryMillis {
  public TimerEveryMillis() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Timer myTimer = new Timer();
    int i = 0;
    while (myStage.getTimer().forMillis(3000)) {
      if (myTimer.everyMillis(500)) {
        myStage.display("Count: " + i++);
      }
    }
  }

  public static void main(String[] args) {
    new TimerEveryMillis();
  }
}
