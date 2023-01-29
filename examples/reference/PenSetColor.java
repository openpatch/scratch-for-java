import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.extensions.Pen;

public class PenSetColor {
    public PenSetColor() {
        Stage myStage = new Stage(254, 100);
        Pen myPen = new Pen();
        myStage.add(myPen);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        myPen.down();
        myPen.setSize(10);
        myPen.setColor(0, 255, 0);
        myPen.setPosition(120, 45);
        myStage.wait(500);
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new PenSetColor();
    }
}