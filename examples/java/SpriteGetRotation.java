import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteGetRotation {
    public SpriteGetRotation() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.setRotation(45);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        mySprite.say("Rotation: " + mySprite.getRotation());
        myStage.wait(3000);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteGetRotation();
    }
}