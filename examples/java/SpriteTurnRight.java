import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteTurnRight {
    public SpriteTurnRight() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "examples/java/assets/zeta_green_badge.png");
        myStage.add(zeta);
        while (true) {
            zeta.turnRight(2);
            myStage.wait(50);
        }
    }

    public static void main(String[] args) {
        new SpriteTurnRight();
    }
}