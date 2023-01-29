import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteChangeY {
    public SpriteChangeY() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(1000);
        mySprite.changeY(-20);
        myStage.wait(1000);
        mySprite.changeY(40);
        myStage.wait(1000);

        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteChangeY();
    }
}
