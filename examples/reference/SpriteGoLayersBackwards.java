import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteGoLayersBackwards {
    public SpriteGoLayersBackwards() {
        Stage myStage = new Stage(254, 100);

        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        myStage.add(new Sprite("zeta", "assets/zeta_green_badge.png"));
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        gamma.changeX(10);
        myStage.add(gamma);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(1000);
        gamma.goLayersBackwards(2);
        myStage.wait(1000);
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteGoLayersBackwards();
    }
}

