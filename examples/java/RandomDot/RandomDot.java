import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class RandomDot extends Stage {
    public RandomDot() {
        super(800, 600);
        this.add(new RandomDotSprite());
    }

    public static void main(String[] args) {
        new RandomDot();
    }
}

class RandomDotSprite extends Sprite {
    public void run() {
        if (this.getTimer().everyMillis(100)) {
            this.getPen().down();
            this.getPen().setSize(10);
            this.setPosition(Math.random() * Window.getInstance().getWidth(),
                    Math.random() * Window.getInstance().getHeight());
            this.getPen().changeColor(2);
            this.getPen().up();
        }
    }
}