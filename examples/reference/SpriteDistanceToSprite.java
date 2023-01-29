import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteDistanceToSprite {
    public SpriteDistanceToSprite() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        gamma.setPosition(0, 50);
        myStage.add(gamma);
        Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
        zeta.setPosition(254, 50);
        myStage.add(zeta);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            gamma.changeX(5);
            zeta.changeX(-5);
            myStage.display("Distance: " + gamma.distanceToSprite(zeta));
            myStage.wait(100);
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteDistanceToSprite();
    }
}
