import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class SpriteMove {
    public SpriteMove() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        myStage.add(gamma);
        gamma.turnLeft(20);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            gamma.move(5);
            myStage.wait(100);
        }
        recorder.stop();
        Window.getInstance().exit();
    }
    public static void main(String[] args) {
        new SpriteMove();
    }
}