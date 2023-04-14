import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class TimerAfterMillis {

    public TimerAfterMillis() {
        Stage myStage = new Stage(254, 100);
        Timer myTimer = new Timer();
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (true) {
            myStage.display("Waiting...");
            if (myTimer.afterMillis(3000)) {
                break;
            }
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new TimerAfterMillis();
    }
}
