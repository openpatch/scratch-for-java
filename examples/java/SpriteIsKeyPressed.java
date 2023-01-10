import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteIsKeyPressed {
    public SpriteIsKeyPressed() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        mySprite.changeX(-80);
        mySprite.changeY(30);
        myStage.add(mySprite);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            mySprite.say("Space pressed? " + mySprite.isKeyPressed(KeyCode.VK_SPACE));
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteIsKeyPressed();
    }
}