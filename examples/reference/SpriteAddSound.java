import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteAddSound {

    public SpriteAddSound() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.addSound("bump", "assets/bump.wav");
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteAddSound();
    }
}
