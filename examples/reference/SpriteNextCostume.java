import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteNextCostume {
    public SpriteNextCostume() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.addCostume("yellow", "assets/zeta_yellow_badge.png");
        myStage.add(zeta);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        zeta.nextCostume();
        myStage.wait(2000);
        zeta.nextCostume();
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    }
    public static void main(String[] args) {
        new SpriteNextCostume();
    }
}
