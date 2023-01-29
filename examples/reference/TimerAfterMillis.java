import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Timer;
import org.openpatch.scratch.extensions.GifRecorder;

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
        System.exit(0);
    }
    public static void main(String[] args) {
        new TimerAfterMillis();
    }
}
