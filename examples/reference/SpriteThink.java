import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteThink {
    public SpriteThink() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.changeY(40);
        zeta.changeX(-100);
        myStage.add(zeta);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        zeta.think("Hi! I'm Zeta and can think line breaks");
        myStage.wait(1000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteThink();
    }
}