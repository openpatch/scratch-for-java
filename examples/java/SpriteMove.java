import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteMove {
    public SpriteMove() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);
        gamma.turnLeft(20);

        while(true) {
            gamma.move(5);
            myStage.wait(100);
        }
    }
    public static void main(String[] args) {
        new SpriteMove();
    }
}