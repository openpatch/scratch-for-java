import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteGoToBackLayer {
    public SpriteGoToBackLayer() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(gamma);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(1000);
        gamma.goToBackLayer();
        myStage.wait(1000);
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteGoToBackLayer();
    }
}

