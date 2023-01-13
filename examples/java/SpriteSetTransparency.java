import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteSetTransparency {
    public SpriteSetTransparency() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "assets/slime.png");
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.add(mySprite);
        myStage.wait(2000);
        mySprite.setTransparency(50);
        myStage.wait(2000);
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteSetTransparency();
    }
}