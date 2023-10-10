import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class TimerForMillis {

  public TimerForMillis() {
    Stage myStage = new Stage(600, 240);
    Timer myTimer = new Timer();
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.display("Start!");
    myStage.wait(500);
    while (myTimer.forMillis(1000)) {
      myStage.display("Running...");
    }
    ;
    myStage.display("Stop!");
    myStage.wait(500);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new TimerForMillis();
  }
}
