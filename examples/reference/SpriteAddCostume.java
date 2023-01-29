import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteAddCostume {
    public SpriteAddCostume() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.addCostume("yellow", "assets/zeta_yellow_badge.png");
        myStage.add(zeta);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.wait(100); 
        recorder.snapshot();

        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteAddCostume();
    }
}