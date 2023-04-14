import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageRun {
    class CustomStage extends Stage {
        public CustomStage() {
            super(254, 100);
            this.setColor(255, 0, 0);
        }

        public void run() {
            this.changeColor(0.5);
        }
    }

    public StageRun() {
        Stage myStage = new CustomStage();
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (myStage.getTimer().forMillis(3000))
            ;
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageRun();
    }
}