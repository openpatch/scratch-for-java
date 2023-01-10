import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageDebug {
    public StageDebug() {
        Stage myStage = new Stage(254, 100);
        myStage.setDebug(true);
        AnimatedSprite bee = new AnimatedSprite();
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        bee.setRotation(45);
        myStage.add(bee);
        myStage.wait(2000);
        recorder.stop();
        System.exit(0);
    } 

    public static void main(String[] args) {
       new StageDebug(); 
    }
}
