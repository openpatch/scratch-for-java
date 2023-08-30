import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.recorder.GifRecorder;

public class SpriteStamp {
    public SpriteStamp() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "assets/slime.png");
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        mySprite.setX(-80);
        recorder.start();
        myStage.wait(3000);
        mySprite.stamp();
        mySprite.changeX(50);
        myStage.wait(3000);
        mySprite.stamp();
        mySprite.changeX(50);
        myStage.wait(3000);
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteStamp();
    }
}
