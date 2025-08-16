package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class TimerEveryMillis {

  public TimerEveryMillis() {
    Stage myStage = new Stage(600, 240);
    Timer myTimer = new Timer();
    int i = 0;
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      if (myTimer.everyMillis(500)) {
        myStage.display("Count: " + i++);
      }
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new TimerEveryMillis();
  }
}
