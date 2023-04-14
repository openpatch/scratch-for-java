import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageChangeTransparency {
    public StageChangeTransparency() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "assets/background_forest.png");
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.changeTransparency(40);
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageChangeTransparency();
    }
}
