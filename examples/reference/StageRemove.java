import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageRemove {
    public StageRemove() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        myStage.add(gamma);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.remove(gamma);
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageRemove();
    }
}
