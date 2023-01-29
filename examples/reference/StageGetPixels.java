import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageGetPixels {
    public StageGetPixels() {
        Stage myStage = new Stage(254,100);
        int[] pixels = myStage.getPixels();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageGetPixels();
    }
}
