import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class SpriteDistanceToMousePointer {
    public SpriteDistanceToMousePointer() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        gamma.setPosition(0, 50);
        myStage.add(gamma);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            gamma.changeX(5);
            myStage.display("Distance: " + gamma.distanceToMousePointer());
            myStage.wait(100);
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteDistanceToMousePointer();
    }
}

