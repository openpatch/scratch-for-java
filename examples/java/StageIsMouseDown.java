import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageIsMouseDown {
    public StageIsMouseDown() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            myStage.display("Mouse down? " + myStage.isMouseDown());
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageIsMouseDown();
    }
}
