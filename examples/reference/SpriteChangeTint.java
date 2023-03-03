import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class SpriteChangeTint {
    public SpriteChangeTint() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();

        myStage.add(mySprite);
        myStage.wait(2000);
        mySprite.changeTint(150);
        myStage.wait(2000);

        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteChangeTint();
    }
}
