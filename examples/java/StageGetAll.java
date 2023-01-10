import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.extensions.Pen;

public class StageGetAll {
    public StageGetAll() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Pen());
        myStage.add(new Pen());
        myStage.add(new Sprite());

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.display("All: " + myStage.getAll().size());
        myStage.wait(2000);
        recorder.snapshot();
        System.exit(0);

    }

    public static void main(String[] args) {
        new StageGetAll();
    }
}
