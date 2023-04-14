import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageGetWidth {
    public StageGetWidth() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.display("Width: " + myStage.getWidth());
        myStage.wait(500);
        recorder.snapshot();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageGetWidth();
    }
}
