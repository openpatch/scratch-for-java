import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageSetColor {
    public StageSetColor() {
        Stage myStage = new Stage(254, 100);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.setColor(200);
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageSetColor();
    }
}