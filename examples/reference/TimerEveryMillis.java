import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class TimerEveryMillis {

    public TimerEveryMillis() {
        Stage myStage = new Stage(254, 100);
        Timer myTimer = new Timer();
        int i = 0;
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (myStage.getTimer().forMillis(3000)) {
            if (myTimer.everyMillis(500)) {
                myStage.display("Count: " + i++);
            }
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new TimerEveryMillis();
    }
}
