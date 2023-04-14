import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class TimerIntervalMillis3 {

  public TimerIntervalMillis3() {
    Stage myStage = new Stage(254, 100);
    Timer myTimer = new Timer();
    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      if (myTimer.intervalMillis(500, true)) {
        myStage.display("Interval 1");
      } else {
        myStage.display("Interval 2");
      }
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new TimerIntervalMillis3();
  }
}
