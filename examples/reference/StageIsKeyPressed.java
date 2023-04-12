import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageIsKeyPressed {
    public StageIsKeyPressed() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            myStage.display("Space pressed? " + myStage.isKeyPressed(KeyCode.VK_SPACE));
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageIsKeyPressed();
    }
}
