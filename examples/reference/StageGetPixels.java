import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class StageGetPixels {
    public StageGetPixels() {
        Stage myStage = new Stage(254,100);
        int[] pixels = myStage.getPixels();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageGetPixels();
    }
}
