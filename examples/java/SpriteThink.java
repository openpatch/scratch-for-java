import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteThink {
    public SpriteThink() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "examples/java/assets/zeta_green_badge.png");
        zeta.changeY(40);
        zeta.changeX(-100);
        myStage.add(zeta);
        zeta.think("Hi! I'm Zeta and can think line breaks");
    }

    public static void main(String[] args) {
        new SpriteThink();
    }
}