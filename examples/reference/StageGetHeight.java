import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class StageGetHeight {
    public StageGetHeight() {
        Stage myStage = new Stage(254,100);
        
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.display("Height: " + myStage.getHeight());
        myStage.wait(500);
        recorder.snapshot();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageGetHeight();
    }
}
