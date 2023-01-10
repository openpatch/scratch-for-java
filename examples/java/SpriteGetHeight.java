import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteGetHeight {
    public SpriteGetHeight() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        mySprite.changeY(30);
        myStage.add(mySprite);
        mySprite.say("Height: " + mySprite.getHeight());
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myStage.wait(1000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteGetHeight();
    }
}