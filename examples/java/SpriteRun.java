import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

class Zeta extends Sprite {
    public Zeta() {
        super("green","examples/java/assets/zeta_green_badge.png");
    }

    @Override
    public void run() {
        this.move(0.1);
    }
}

public class SpriteRun {
    public SpriteRun() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Zeta());
    }
    public static void main(String[] args) {
        new SpriteRun();
    }
}