import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageTimer {
    public StageTimer() {
        Stage myStage = new Stage(254, 100);
        myStage.addTimer("identityChange");

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(6000)) {
            if(myStage.getTimer("identityChange").everyMillis(1000)) {
                myStage.changeColor(40);
            }
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageTimer();
    }
}