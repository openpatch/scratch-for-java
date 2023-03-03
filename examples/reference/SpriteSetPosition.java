import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class SpriteSetPosition {
    public SpriteSetPosition() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "assets/slime.png");
        myStage.add(mySprite);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            int x = myStage.pickRandom(0, myStage.getWidth());
            int y = myStage.pickRandom(0, myStage.getHeight());

            mySprite.setPosition(x, y);
            myStage.wait(100);
        }
        recorder.stop();
        Window.getInstance().exit();

    }

    public static void main(String[] args) {
        new SpriteSetPosition();
    }
}