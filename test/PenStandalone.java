import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class PenStandalone {
    public static void main(String[] args) {
        new Stage(400, 400);
        Pen p = new Pen();
        p.down();
        p.setPosition(40, 40);
        p.setPosition(40, 100);
        p.draw();
    }
}
