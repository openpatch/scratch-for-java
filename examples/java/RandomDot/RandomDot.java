package RandomDot;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

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
            this.setPosition(Math.random() * Stage.getInstance().getWidth(),
                    Math.random() * Stage.getInstance().getHeight());
            this.getPen().changeColor(2);
            this.getPen().up();
        }
    }
}