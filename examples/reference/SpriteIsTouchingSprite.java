import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsTouchingSprite {
    public SpriteIsTouchingSprite() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        gamma.setPosition(0, 50);
        myStage.add(gamma);
        Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
        zeta.setPosition(254, 50);
        myStage.add(zeta);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(!gamma.isTouchingSprite(zeta)) {
            gamma.changeX(5);
            zeta.changeX(-5);
            myStage.wait(100);
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteIsTouchingSprite();
    }
}

