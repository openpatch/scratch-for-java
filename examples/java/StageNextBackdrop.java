import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageNextBackdrop {
    public StageNextBackdrop() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "assets/background_forest.png");
        myStage.addBackdrop("sea", "assets/background_sea.png");
        
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.nextBackdrop();
        myStage.wait(2000);
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageNextBackdrop();
    }
}
