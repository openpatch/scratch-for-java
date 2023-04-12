import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageDebug {
    public StageDebug() {
        Stage myStage = new Stage(254, 100);
        myStage.setDebug(true);
        AnimatedSprite bee = new AnimatedSprite();
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        bee.setDirection(45);
        myStage.add(bee);
        myStage.wait(2000);
        recorder.stop();
        Window.getInstance().exit();
    } 

    public static void main(String[] args) {
       new StageDebug(); 
    }
}
