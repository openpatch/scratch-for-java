import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class Drawable {
    public static void main(String[] args) {
        Stage s = new Stage(400, 400);
        Pen p = new Pen();
        s.add(p);
        p.down();
        p.setPosition(0, 0);
        p.setPosition(200, 200);
        p.setPosition(0, 400);
    }
}
