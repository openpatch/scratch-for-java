import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageRemoveAll {
    public StageRemoveAll() {
        Stage myStage = new Stage(254,100);
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        gamma.changeX(20);
        myStage.add(gamma);
        Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
        zeta.changeX(-20);
        myStage.add(zeta);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        myStage.removeAll();
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();

    }

    public static void main(String[] args) {
        new StageRemoveAll();
    }
}
