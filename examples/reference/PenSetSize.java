import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.Pen;

public class PenSetSize {
    public PenSetSize() {
        Stage myStage = new Stage(254, 100);
        Pen myPen = new Pen();
        myStage.add(myPen);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myPen.setSize(10);
        while (myStage.getTimer().forMillis(3000)) {
            myPen.down();
            if (myStage.getTimer().everyMillis(500)) {
                myPen.setSize(myStage.pickRandom(5, 20));
            }
            myPen.setPosition(myStage.pickRandom(0, myStage.getWidth()), 45);
            myPen.up();
            myStage.wait(200);
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new PenSetSize();
    }
}

