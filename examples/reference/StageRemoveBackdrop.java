import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageRemoveBackdrop {
    public StageRemoveBackdrop() {
        Stage myStage = new Stage(254,100);
        myStage.addBackdrop("forest", "assets/background_forest.png");
        myStage.addBackdrop("sea", "assets/background_sea.png");
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.removeBackdrop("forest");
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageRemoveBackdrop();
    }
}