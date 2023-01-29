import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageGetWidth {
    public StageGetWidth() {
        Stage myStage = new Stage(254,100);
        
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.display("Width: " + myStage.getWidth());
        myStage.wait(500);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageGetWidth();
    }
}
