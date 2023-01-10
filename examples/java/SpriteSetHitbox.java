import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteSetHitbox {
    public SpriteSetHitbox() {
        Stage myStage = new Stage(254, 100);
        myStage.setDebug(true);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        int x[] = { 0, 10, 10, 0 };
        int y[] = { 0, 0, 10, 10 };
        mySprite.setHitbox(x, y);
        recorder.stop();
        myStage.wait(2000);
        System.exit(0);
    }
    public static void main(String[] args) {
        new SpriteSetHitbox();
    }
}