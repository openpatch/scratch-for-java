import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageAdd {
    public StageAdd() {
        Stage myStage = new Stage(254, 100);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.add(new Sprite("cat", "docs/en/public/assets/logo.png"));
        myStage.wait(2000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageAdd();
    }
}
