import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageAddBackdrop {
    public StageAddBackdrop() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "assets/background_forest.png");
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.wait(1000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageAddBackdrop();
    }
}
