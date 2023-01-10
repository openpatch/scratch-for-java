import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageDisplay {
    public StageDisplay() {
        Stage myStage = new Stage(254, 100);
        myStage.display("Welcome! This is a longer text with auto line breaks!", 2000);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.wait(2000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageDisplay();
    }
}
