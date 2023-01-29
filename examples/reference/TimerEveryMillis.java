import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Timer;
import org.openpatch.scratch.extensions.GifRecorder;

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
        System.exit(0);
    }
    public static void main(String[] args) {
        new TimerEveryMillis();
    }
}
