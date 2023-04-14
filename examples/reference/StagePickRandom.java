import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StagePickRandom {
    public StagePickRandom() {
        Stage myStage = new Stage(254, 100);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (myStage.getTimer().forMillis(3000)) {
            int random = myStage.pickRandom(0, 100);
            myStage.display("Random: " + random);
            myStage.wait(500);
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StagePickRandom();
    }
}
